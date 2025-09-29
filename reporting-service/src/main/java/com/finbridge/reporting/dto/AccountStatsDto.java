package com.finbridge.reporting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;

@Schema(description = "Статистика аккаунтов за день")
public class AccountStatsDto {

    @Schema(description = "Дата отчёта", example = "2025-09-29")
    private LocalDate date;

    @Schema(description = "Активных аккаунтов", example = "350")
    private long activeAccounts;

    // Геттеры и сеттеры

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getActiveAccounts() {
        return activeAccounts;
    }

    public void setActiveAccounts(long activeAccounts) {
        this.activeAccounts = activeAccounts;
    }
}
