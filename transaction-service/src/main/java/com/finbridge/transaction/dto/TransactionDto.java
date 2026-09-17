package com.finbridge.transaction.dto;

import com.finbridge.transaction.entity.Transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionDto {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String currency;
    private LocalDateTime timestamp;
    private String status;

    public TransactionDto() {
    }

    public TransactionDto(Transaction tx) {
        this.id = tx.getId();
        this.fromAccountId = tx.getFromAccountId();
        this.toAccountId = tx.getToAccountId();
        this.amount = tx.getAmount();
        this.currency = tx.getCurrency();
        this.timestamp = tx.getTimestamp();
        this.status = tx.getStatus();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromAccountId() {
        return fromAccountId;
    }

    public void setFromAccountId(Long fromAccountId) {
        this.fromAccountId = fromAccountId;
    }

    public Long getToAccountId() {
        return toAccountId;
    }

    public void setToAccountId(Long toAccountId) {
        this.toAccountId = toAccountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
