package com.finbridge.transaction.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@FeignClient(name = "account-service", url = "${account.service.url}")
public interface AccountServiceClient {

    @PostMapping("/api/v1/accounts/{id}/debit")
    void debit(@PathVariable("id") Long accountId,
               @RequestParam("amount") BigDecimal amount);

    @PostMapping("/api/v1/accounts/{id}/credit")
    void credit(@PathVariable("id") Long accountId,
                @RequestParam("amount") BigDecimal amount);

    @GetMapping("/api/v1/accounts/{id}/balance")
    BigDecimal getBalance(@PathVariable("id") Long accountId);
}
