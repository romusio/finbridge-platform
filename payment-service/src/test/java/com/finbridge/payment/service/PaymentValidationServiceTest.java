package com.finbridge.payment.service;

import com.finbridge.payment.client.AccountServiceClient;
import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.exception.InsufficientFundsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;

class PaymentValidationServiceTest {

    private AccountServiceClient accountClient;
    private PaymentValidationService validationService;

    @BeforeEach
    void setup() {
        accountClient = Mockito.mock(AccountServiceClient.class);
        validationService = new PaymentValidationService(accountClient);
    }

    @Test
    void validatePaymentRequest_exceedsMaxAmount_throws() {
        PaymentRequest req = new PaymentRequest();
        req.setAmount(new BigDecimal("1000000.00"));
        Exception ex = assertThrows(IllegalArgumentException.class,
                () -> validationService.validatePaymentRequest(req));
        assertTrue(ex.getMessage().contains("превышает"));
    }

    @Test
    void validateAccountBalance_insufficient_throws() {
        Mockito.when(accountClient.getBalance(anyLong()))
                .thenReturn(new BigDecimal("50.00"));
        assertThrows(InsufficientFundsException.class,
                () -> validationService.validateAccountBalance(1L, new BigDecimal("100.00")));
    }

    @Test
    void validateAccountBalance_sufficient_passes() {
        Mockito.when(accountClient.getBalance(anyLong()))
                .thenReturn(new BigDecimal("500.00"));
        assertDoesNotThrow(() ->
                validationService.validateAccountBalance(1L, new BigDecimal("100.00")));
    }
}
