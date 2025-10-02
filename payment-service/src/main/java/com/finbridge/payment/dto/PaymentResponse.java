package com.finbridge.payment.dto;

import com.finbridge.payment.entity.PaymentMethod;
import com.finbridge.payment.entity.PaymentStatus;
import lombok.Data;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class PaymentResponse {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private String currency;
    private String description;
    private PaymentMethod paymentMethod;
    private PaymentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private Long transactionId;
}
