package com.finbridge.payment.service;

import com.finbridge.payment.client.TransactionServiceClient;
import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.dto.PaymentResponse;
import com.finbridge.payment.entity.Payment;
import com.finbridge.payment.entity.PaymentStatus;
import com.finbridge.payment.exception.PaymentNotFoundException;
import com.finbridge.payment.repository.PaymentRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository repo;
    private final PaymentValidationService validator;
    private final TransactionServiceClient txClient;

    @Transactional
    @CircuitBreaker(name = "payment-service")
    @Retry(name = "payment-service")
    public PaymentResponse createPayment(PaymentRequest req) {
        validator.validatePaymentRequest(req);
        validator.validateAccountBalance(req.getFromAccountId(), req.getAmount());

        Payment p = Payment.builder()
                .fromAccountId(req.getFromAccountId())
                .toAccountId(req.getToAccountId())
                .amount(req.getAmount())
                .currency(req.getCurrency())
                .description(req.getDescription())
                .status(PaymentStatus.PENDING)
                .build();
        p = repo.save(p);

        Long txId = txClient.createTransaction(
                new TransactionServiceClient.TransactionRequest(
                        p.getFromAccountId(), p.getToAccountId(), p.getAmount(), p.getDescription()
                )
        );
        p.setStatus(PaymentStatus.COMPLETED);
        p.setTransactionId(txId);
        p.setProcessedAt(LocalDateTime.now());
        repo.save(p);

        return map(p);
    }

    public PaymentResponse getById(Long id) {
        Payment p = repo.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
        return map(p);
    }

    public PaymentResponse cancelPayment(Long id) {
        Payment p = repo.findById(id).orElseThrow(() -> new PaymentNotFoundException(id));
        if (!p.getStatus().equals(PaymentStatus.PENDING)) {
            throw new IllegalStateException("Нельзя отменить платеж в текущем статусе");
        }
        p.setStatus(PaymentStatus.CANCELLED);
        repo.save(p);
        return map(p);
    }

    private PaymentResponse map(Payment p) {
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
