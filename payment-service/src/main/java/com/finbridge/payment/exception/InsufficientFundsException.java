package com.finbridge.payment.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(Long accountId, BigDecimal amount, BigDecimal balance) {
        super("Недостаточно средств на счёте " + accountId +
                ": требуется " + amount + ", доступно " + balance);
    }
}
