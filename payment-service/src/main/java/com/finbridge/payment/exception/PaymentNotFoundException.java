package com.finbridge.payment.exception;

public class PaymentNotFoundException extends RuntimeException {
    public PaymentNotFoundException(Long id) {
        super("Платеж не найден: " + id);
    }
}
