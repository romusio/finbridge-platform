package com.finbridge.payment.controller;

import com.finbridge.payment.PaymentServiceApplication;
import com.finbridge.payment.dto.PaymentRequest;
import com.finbridge.payment.dto.PaymentResponse;
import com.finbridge.payment.entity.PaymentMethod;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = PaymentServiceApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate rest;

    @Test
    void healthCheck_returnsOk() {
        ResponseEntity<String> resp = rest.getForEntity(
                "http://localhost:" + port + "/api/v1/payments/health",
                String.class
        );
        assertEquals(HttpStatus.OK, resp.getStatusCode());
        assertTrue(resp.getBody().contains("running"));
    }

    @Test
    void createAndGetPayment_flow() {
        PaymentRequest req = new PaymentRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(new BigDecimal("10.00"));
        req.setCurrency("RUB");
        req.setDescription("Integration Test");
        req.setPaymentMethod(PaymentMethod.CARD);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentRequest> entity = new HttpEntity<>(req, headers);

        ResponseEntity<PaymentResponse> createResp = rest.exchange(
                "http://localhost:" + port + "/api/v1/payments",
                HttpMethod.POST,
                entity,
                PaymentResponse.class
        );
        assertEquals(HttpStatus.CREATED, createResp.getStatusCode());
        Long id = createResp.getBody().getId();

        ResponseEntity<PaymentResponse> getResp = rest.getForEntity(
                "http://localhost:" + port + "/api/v1/payments/" + id,
                PaymentResponse.class
        );
        assertEquals(HttpStatus.OK, getResp.getStatusCode());
        assertEquals("Integration Test", getResp.getBody().getDescription());
    }
}
