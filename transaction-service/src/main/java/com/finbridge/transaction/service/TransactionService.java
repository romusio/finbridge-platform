package com.finbridge.transaction.service;

import com.finbridge.transaction.dto.TransactionDto;
import com.finbridge.transaction.dto.TransactionRequest;
import com.finbridge.transaction.entity.Transaction;
import com.finbridge.transaction.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository txRepo;

    @Transactional
    public TransactionDto createTransaction(TransactionRequest req) {
        Transaction tx = new Transaction();
        tx.setFromAccountId(req.getFromAccountId());
        tx.setToAccountId(req.getToAccountId());
        tx.setAmount(req.getAmount());
        tx.setCurrency(req.getCurrency());
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus("PENDING");
        Transaction saved = txRepo.save(tx);

        // TODO: call Account Service to debit and credit balances

        saved.setStatus("COMPLETED");
        saved = txRepo.save(saved);

        return new TransactionDto(saved);
    }

    public List<TransactionDto> getTransactionsByAccount(Long accountId) {
        List<Transaction> list = txRepo.findByFromAccountIdOrToAccountId(accountId, accountId);
        return list.stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }
}
