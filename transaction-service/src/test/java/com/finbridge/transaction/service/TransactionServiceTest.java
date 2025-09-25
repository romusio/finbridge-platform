package com.finbridge.transaction.service;

import com.finbridge.transaction.client.AccountServiceClient;
import com.finbridge.transaction.dto.TransactionRequest;
import com.finbridge.transaction.dto.TransactionDto;
import com.finbridge.transaction.entity.Transaction;
import com.finbridge.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @InjectMocks
    private TransactionService txService;

    @Mock
    private TransactionRepository txRepo;

    @Mock
    private AccountServiceClient accountClient;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSuccessfulTransaction() {
        TransactionRequest req = new TransactionRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(new BigDecimal("100"));
        req.setCurrency("RUB");

        when(txRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountClient.getBalance(1L)).thenReturn(new BigDecimal("200"));
        doNothing().when(accountClient).debit(1L, req.getAmount());
        doNothing().when(accountClient).credit(2L, req.getAmount());

        TransactionDto dto = txService.createTransaction(req);
        assertThat(dto.getStatus()).isEqualTo("COMPLETED");
        verify(accountClient).debit(1L, req.getAmount());
        verify(accountClient).credit(2L, req.getAmount());
    }

    @Test
    void testFailedTransactionInsufficientFunds() {
        TransactionRequest req = new TransactionRequest();
        req.setFromAccountId(1L);
        req.setToAccountId(2L);
        req.setAmount(new BigDecimal("300"));
        req.setCurrency("RUB");

        when(txRepo.save(any(Transaction.class))).thenAnswer(inv -> inv.getArgument(0));
        when(accountClient.getBalance(1L)).thenReturn(new BigDecimal("200"));

        TransactionDto dto = txService.createTransaction(req);
        assertThat(dto.getStatus()).isEqualTo("FAILED");
        verify(accountClient, never()).debit(anyLong(), any());
    }
}
