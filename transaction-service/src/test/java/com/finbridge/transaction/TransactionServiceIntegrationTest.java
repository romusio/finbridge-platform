package com.finbridge.transaction;

import com.finbridge.transaction.dto.TransactionRequest;
import com.finbridge.transaction.dto.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionServiceIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private HttpHeaders headers;
    private Long srcId;
    private Long dstId;

    @BeforeEach
    void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Создаём счета
        var req1 = Map.of(
                "userId", 1L,
                "accountName", "Src",
                "accountType", "CHECKING"
        );
        var req2 = Map.of(
                "userId", 1L,
                "accountName", "Dst",
                "accountType", "SAVINGS"
        );

        // Получаем ответ как Map<String,Object>
        Map<String,Object> response1 = rest.postForEntity(
                "/api/v1/accounts",
                new HttpEntity<>(req1, headers),
                Map.class
        ).getBody();
        srcId = ((Number) response1.get("id")).longValue();

        Map<String,Object> response2 = rest.postForEntity(
                "/api/v1/accounts",
                new HttpEntity<>(req2, headers),
                Map.class
        ).getBody();
        dstId = ((Number) response2.get("id")).longValue();

        // Пополняем src счёт
        rest.postForEntity(
                "/api/v1/accounts/{id}/credit?amount=1000",
                null,
                Void.class,
                srcId
        );
    }


    @Test
    void testTransactionFlow() {
        // Создание транзакции
        TransactionRequest txReq = new TransactionRequest();
        txReq.setFromAccountId(srcId);
        txReq.setToAccountId(dstId);
        txReq.setAmount(new BigDecimal("250"));
        txReq.setCurrency("RUB");

        ResponseEntity<TransactionDto> txResp = rest.postForEntity(
                "/api/v1/transactions", new HttpEntity<>(txReq, headers), TransactionDto.class);
        assertThat(txResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(txResp.getBody().getStatus()).isEqualTo("COMPLETED");

        // Балансы
        var balSrc = rest.getForEntity("/api/v1/accounts/{id}/balance", BigDecimal.class, srcId).getBody();
        var balDst = rest.getForEntity("/api/v1/accounts/{id}/balance", BigDecimal.class, dstId).getBody();
        assertThat(balSrc).isEqualByComparingTo(new BigDecimal("750"));
        assertThat(balDst).isEqualByComparingTo(new BigDecimal("250"));

        // История
        ResponseEntity<List> history = rest.exchange(
                "/api/v1/transactions/account/{id}", HttpMethod.GET, new HttpEntity<>(headers), List.class, srcId);
        assertThat(history.getBody()).isNotEmpty();
    }
}
