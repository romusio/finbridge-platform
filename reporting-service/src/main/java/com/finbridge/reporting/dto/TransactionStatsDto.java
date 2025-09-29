package com.finbridge.reporting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Статистика транзакций за день")
public class TransactionStatsDto {

    @Schema(description = "Дата отчёта", example = "2025-09-29")
    private LocalDate date;

    @Schema(description = "Всего транзакций", example = "1500")
    private long totalTransactions;

    @Schema(description = "Общий объём транзакций", example = "250000.50")
    private BigDecimal totalVolume;

    // Геттеры и сеттеры

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public long getTotalTransactions() {
        return totalTransactions;
    }

    public void setTotalTransactions(long totalTransactions) {
        this.totalTransactions = totalTransactions;
    }

    public BigDecimal getTotalVolume() {
        return totalVolume;
    }

    public void setTotalVolume(BigDecimal totalVolume) {
        this.totalVolume = totalVolume;
    }
}
