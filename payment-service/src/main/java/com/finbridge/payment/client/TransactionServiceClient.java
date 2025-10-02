package com.finbridge.payment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.math.BigDecimal;

@FeignClient(name = "transaction-service", url = "${external.services.transaction-service.url}")
public interface TransactionServiceClient {
    @PostMapping("/api/v1/transactions")
    Long createTransaction(TransactionRequest request);

    record TransactionRequest(Long fromAccountId, Long toAccountId, BigDecimal amount, String description) {}
}
