package com.finbridge.reporting.controller;

import com.finbridge.reporting.dto.AccountStatsDto;
import com.finbridge.reporting.dto.TransactionStatsDto;
import com.finbridge.reporting.client.AccountClient;
import com.finbridge.reporting.client.TransactionClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/stats")
@Tag(name = "Stats", description = "Сырые статистики Account и Transaction Service")
public class StatsController {

    private final TransactionClient txClient;
    private final AccountClient acctClient;

    public StatsController(TransactionClient txClient, AccountClient acctClient) {
        this.txClient = txClient;
        this.acctClient = acctClient;
    }

    @Operation(
            summary = "Статистика транзакций",
            description = "Возвращает статистику транзакций за последний день"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Статистика транзакций",
            content = @Content(schema = @Schema(implementation = TransactionStatsDto.class))
    )
    @GetMapping("/transactions")
    public TransactionStatsDto getTransactionStats() {
        return txClient.getStats();
    }

    @Operation(
            summary = "Статистика аккаунтов",
            description = "Возвращает статистику активных аккаунтов за последний день"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Статистика аккаунтов",
            content = @Content(schema = @Schema(implementation = AccountStatsDto.class))
    )
    @GetMapping("/accounts")
    public AccountStatsDto getAccountStats() {
        return acctClient.getStats();
    }
}
