package com.finbridge.reporting.client;

import com.finbridge.reporting.dto.AccountStatsDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "account-service",
        url = "${account.service.url}"
)
public interface AccountClient {

    @GetMapping("/api/v1/accounts/stats")
    AccountStatsDto getStats();
}
