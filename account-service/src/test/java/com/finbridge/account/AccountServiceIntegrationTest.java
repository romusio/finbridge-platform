package com.finbridge.account;

import com.finbridge.account.dto.CreateAccountRequest;
import com.finbridge.account.dto.AccountDto;
import com.finbridge.account.entity.Account.AccountType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountServiceIntegrationTest {

    @Autowired
    private TestRestTemplate rest;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testCreateDebitCreditBalance() {
        // 1. Создать счёт
        CreateAccountRequest req = new CreateAccountRequest();
        req.setUserId(1L);
        req.setAccountName("TestAcc");
        req.setAccountType(AccountType.CHECKING);
        HttpEntity<CreateAccountRequest> create = new HttpEntity<>(req, headers);

        ResponseEntity<AccountDto> createResp = rest.postForEntity(
                "/api/v1/accounts", create, AccountDto.class);
        assertThat(createResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        AccountDto acc = createResp.getBody();
        assertThat(acc.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        Long id = acc.getId();

        // 2. Кредит
        rest.postForEntity(
                "/api/v1/accounts/{id}/credit?amount=500", null, Void.class, id);

        // 3. Получение баланса через AccountDto
        ResponseEntity<AccountDto> balDto1 = rest.getForEntity(
                "/api/v1/accounts/{id}", AccountDto.class, id);
        assertThat(balDto1.getBody().getBalance())
                .isEqualByComparingTo(new BigDecimal("500"));

        // 4. Дебет
        rest.postForEntity(
                "/api/v1/accounts/{id}/debit?amount=200", null, Void.class, id);

        // 5. После дебета:
        ResponseEntity<AccountDto> balDto2 = rest.getForEntity(
                "/api/v1/accounts/{id}", AccountDto.class, id);
        assertThat(balDto2.getBody().getBalance())
                .isEqualByComparingTo(new BigDecimal("300"));

    }
}
