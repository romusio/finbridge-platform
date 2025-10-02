package com.finbridge.payment.service;

import com.finbridge.payment.client.AccountServiceClient;
import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.exception.InsufficientFundsException;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentValidationService {
    private final AccountServiceClient accountClient;
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("100000.00");

    public void validatePaymentRequest(PaymentRequest req) {
        if (req.getAmount().compareTo(MAX_AMOUNT) > 0) {
            throw new IllegalArgumentException("Сумма превышает максимальный лимит");
        }
    }

    public void validateAccountBalance(Long accountId, BigDecimal amount) {
        BigDecimal balance = accountClient.getBalance(accountId);
        if (balance.compareTo(amount) < 0) {
            throw new InsufficientFundsException(accountId, amount, balance);
        }
    }
}
