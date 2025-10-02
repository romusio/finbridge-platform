package com.finbridge.payment.entity;

public enum PaymentMethod {
    BANK_TRANSFER("Банковский перевод"),
    CARD("Платежная карта"),
    SBP("СБП"),
    WALLET("Электронный кошелек");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
