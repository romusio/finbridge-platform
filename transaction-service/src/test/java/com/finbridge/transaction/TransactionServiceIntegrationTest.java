package com.finbridge.transaction;

import com.finbridge.transaction.client.AccountServiceClient;
import com.finbridge.transaction.dto.TransactionDto;
import com.finbridge.transaction.dto.TransactionRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TransactionServiceIntegrationTest {

    private static final Long SOURCE_ACCOUNT_ID = 101L;
    private static final Long DESTINATION_ACCOUNT_ID = 202L;

    @Autowired
    private TestRestTemplate rest;

    @MockBean
    private AccountServiceClient accountClient;

    private HttpHeaders headers;

    @BeforeEach
    void setup() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        when(accountClient.getBalance(SOURCE_ACCOUNT_ID)).thenReturn(new BigDecimal("1000.00"));
    }

    @Test
    void createsCompletedTransactionAndReturnsItInAccountHistory() {
        TransactionRequest request = new TransactionRequest();
        request.setFromAccountId(SOURCE_ACCOUNT_ID);
        request.setToAccountId(DESTINATION_ACCOUNT_ID);
        request.setAmount(new BigDecimal("250.00"));
        request.setCurrency("RUB");

        ResponseEntity<TransactionDto> response = rest.postForEntity(
                "/api/v1/transactions",
                new HttpEntity<>(request, headers),
                TransactionDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getStatus()).isEqualTo("COMPLETED");
        assertThat(response.getBody().getFromAccountId()).isEqualTo(SOURCE_ACCOUNT_ID);
        assertThat(response.getBody().getToAccountId()).isEqualTo(DESTINATION_ACCOUNT_ID);
        assertThat(response.getBody().getAmount()).isEqualByComparingTo("250.00");

        verify(accountClient).getBalance(SOURCE_ACCOUNT_ID);
        verify(accountClient).debit(SOURCE_ACCOUNT_ID, new BigDecimal("250.00"));
        verify(accountClient).credit(DESTINATION_ACCOUNT_ID, new BigDecimal("250.00"));

        ResponseEntity<List> history = rest.exchange(
                "/api/v1/transactions/account/{id}",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                List.class,
                SOURCE_ACCOUNT_ID
        );

        assertThat(history.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(history.getBody()).isNotNull().isNotEmpty();
    }
}
