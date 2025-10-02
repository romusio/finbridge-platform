package com.finbridge.payment.dto;

import com.finbridge.payment.entity.PaymentMethod;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {
    @NotNull(message = "ID счета отправителя обязателен")
    private Long fromAccountId;

    @NotNull(message = "ID счета получателя обязателен")
    private Long toAccountId;

    @NotNull(message = "Сумма обязательна")
    @Positive(message = "Сумма должна быть положительной")
    @DecimalMax(value = "999999.99", message = "Сумма не может превышать 999,999.99")
    private BigDecimal amount;

    @Pattern(regexp = "^[A-Z]{3}$", message = "Валюта должна быть в формате ISO (например, RUB)")
    private String currency = "RUB";

    @Size(max = 500, message = "Описание не может превышать 500 символов")
    private String description;

    @NotNull(message = "Метод платежа обязателен")
    private PaymentMethod paymentMethod;
}

