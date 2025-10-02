package com.finbridge.payment.entity;

public enum PaymentStatus {
    PENDING("Ожидает обработки"),
    PROCESSING("В обработке"),
    COMPLETED("Завершен"),
    FAILED("Ошибка"),
    CANCELLED("Отменен");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
