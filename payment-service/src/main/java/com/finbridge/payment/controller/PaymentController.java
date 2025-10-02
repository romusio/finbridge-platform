package com.finbridge.payment.controller;

import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.dto.PaymentResponse;
import com.finbridge.payment.entity.Payment;
import com.finbridge.payment.repository.PaymentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Payment Controller", description = "API для управления платежами FinBridge")
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @PostMapping
    @Operation(summary = "Создать новый платеж", description = "Создает платеж с валидацией данных")
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest request) {
        log.info("Создание платежа: {} -> {}, сумма {} {}",
                request.getFromAccountId(), request.getToAccountId(),
                request.getAmount(), request.getCurrency());

        // Преобразовать DTO в Entity
        Payment payment = Payment.builder()
                .fromAccountId(request.getFromAccountId())
                .toAccountId(request.getToAccountId())
                .amount(request.getAmount())
                .currency(request.getCurrency())
                .description(request.getDescription())
                .paymentMethod(request.getPaymentMethod())
                .build();
        // Сохранить в БД
        Payment saved = paymentRepository.save(payment);

        // Преобразовать Entity в DTO
        PaymentResponse response = mapToResponse(saved);
        log.info("Платеж создан с ID: {}", response.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить платеж по ID", description = "Возвращает информацию о платеже")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable Long id) {
        log.info("Запрос платежа ID={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return ResponseEntity.ok(mapToResponse(payment));
    }

    @GetMapping
    @Operation(summary = "Получить все платежи", description = "Список всех платежей")
    public ResponseEntity<List<PaymentResponse>> getAllPayments() {
        log.info("Запрос всех платежей");
        List<PaymentResponse> list = paymentRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/account/{accountId}")
    @Operation(summary = "Платежи по счёту", description = "Входящие и исходящие платежи для счёта")
    public ResponseEntity<List<PaymentResponse>> getByAccount(@PathVariable Long accountId) {
        log.info("Запрос платежей для счёта {}", accountId);
        List<PaymentResponse> list = paymentRepository.findByAccountId(accountId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}/status")
    @Operation(summary = "Статус платежа", description = "Текущий статус платежа")
    public ResponseEntity<String> getStatus(@PathVariable Long id) {
        log.info("Запрос статуса платежа ID={}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Payment not found: " + id));
        return ResponseEntity.ok(payment.getStatus().name());
    }

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Проверка доступности сервиса")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Payment Service is running on port 8086");
    }

    // Вспомогательный метод для преобразования
    private PaymentResponse mapToResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .fromAccountId(p.getFromAccountId())
                .toAccountId(p.getToAccountId())
                .amount(p.getAmount())
                .currency(p.getCurrency())
                .description(p.getDescription())
                .paymentMethod(p.getPaymentMethod())
                .status(p.getStatus())
                .createdAt(p.getCreatedAt())
                .processedAt(p.getProcessedAt())
                .transactionId(p.getTransactionId())
                .build();
    }
}
