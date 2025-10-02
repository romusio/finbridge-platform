package com.finbridge.payment.service;

import com.finbridge.payment.client.TransactionServiceClient;
import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.dto.PaymentResponse;
import com.finbridge.payment.entity.Payment;
import com.finbridge.payment.entity.PaymentMethod;
import com.finbridge.payment.entity.PaymentStatus;
import com.finbridge.payment.exception.PaymentNotFoundException;
import com.finbridge.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

class PaymentServiceTest {

    private PaymentRepository repo;
    private PaymentValidationService validator;
    private TransactionServiceClient txClient;
    private PaymentService service;

    @BeforeEach
    void setup() {
        repo = Mockito.mock(PaymentRepository.class);
        validator = Mockito.mock(PaymentValidationService.class);
        txClient = Mockito.mock(TransactionServiceClient.class);
        service = new PaymentService(repo, validator, txClient);
    }

    @Test
    void createPayment_successful_flow() {
        PaymentRequest req = new PaymentRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(new BigDecimal("100.00"));
        req.setCurrency("RUB");
        req.setDescription("Test");
        req.setPaymentMethod(PaymentMethod.CARD);

        Mockito.doNothing().when(validator).validatePaymentRequest(req);
        Mockito.doNothing().when(validator).validateAccountBalance(1L, new BigDecimal("100.00"));
        Mockito.when(repo.save(any(Payment.class))).thenAnswer(inv -> inv.getArgument(0));
        Mockito.when(txClient.createTransaction(any())).thenReturn(123L);

        PaymentResponse resp = service.createPayment(req);

        assertEquals(PaymentStatus.COMPLETED, resp.getStatus());
        assertEquals(123L, resp.getTransactionId());
    }

    @Test
    void getById_notFound_throws() {
        Mockito.when(repo.findById(1L)).thenReturn(Optional.empty());
        assertThrows(PaymentNotFoundException.class, () -> service.getById(1L));
    }
}
