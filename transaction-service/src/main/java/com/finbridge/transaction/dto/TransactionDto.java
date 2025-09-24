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

    public TransactionDto(Transaction tx) {
        this.id = tx.getId();
        this.fromAccountId = tx.getFromAccountId();
        this.toAccountId = tx.getToAccountId();
        this.amount = tx.getAmount();
        this.currency = tx.getCurrency();
        this.timestamp = tx.getTimestamp();
        this.status = tx.getStatus();
    }

    public Long getId() { return id; }
    public Long getFromAccountId() { return fromAccountId; }
    public Long getToAccountId() { return toAccountId; }
    public BigDecimal getAmount() { return amount; }
    public String getCurrency() { return currency; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getStatus() { return status; }
}
