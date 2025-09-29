package com.finbridge.reporting.client;

import com.finbridge.reporting.dto.TransactionStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "transaction-service",
        url = "${transaction.service.url}"
)
public interface TransactionClient {

    @GetMapping("/api/v1/transactions/stats")
    TransactionStatsDto getStats();
}
