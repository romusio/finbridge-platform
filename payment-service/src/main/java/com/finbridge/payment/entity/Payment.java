package com.finbridge.payment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "from_account_id")
    private Long fromAccountId;

    @NotNull
    @Column(name = "to_account_id")
    private Long toAccountId;

    @NotNull
    @Positive
    @Column(precision = 19, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Column(length = 3)
    private String currency = "RUB";

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    private PaymentMethod paymentMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PaymentStatus status = PaymentStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "transaction_id")
    private Long transactionId;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
