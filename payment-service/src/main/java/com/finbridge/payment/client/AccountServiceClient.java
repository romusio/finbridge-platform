package com.finbridge.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import java.math.BigDecimal;
import java.util.List;

@FeignClient(name = "account-service", url = "${external.services.account-service.url}")
public interface AccountServiceClient {
    @GetMapping("/api/v1/accounts/{accountId}/balance")
    BigDecimal getBalance(@PathVariable("accountId") Long accountId);

    @GetMapping("/api/v1/users/{userId}/accounts")
    List<Long> getUserAccountIds(@PathVariable("userId") Long userId);
}
