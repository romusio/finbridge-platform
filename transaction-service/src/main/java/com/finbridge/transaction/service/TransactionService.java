package com.finbridge.transaction.service;

import com.finbridge.transaction.client.AccountServiceClient;
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
import java.math.BigDecimal;


@Service
public class TransactionService {

    @Autowired
    private TransactionRepository txRepo;

    @Autowired
    private AccountServiceClient accountClient;

    @Transactional
    public TransactionDto createTransaction(TransactionRequest req) {
        // 1. Создание записи транзакции со статусом PENDING
        Transaction tx = new Transaction();
        tx.setFromAccountId(req.getFromAccountId());
        tx.setToAccountId(req.getToAccountId());
        tx.setAmount(req.getAmount());
        tx.setCurrency(req.getCurrency());
        tx.setTimestamp(LocalDateTime.now());
        tx.setStatus("PENDING");
        tx = txRepo.save(tx);

        try {
            // 2. Проверяем баланс отправителя
            BigDecimal fromBalance = accountClient.getBalance(req.getFromAccountId());
            if (fromBalance.compareTo(req.getAmount()) < 0) {
                throw new RuntimeException("Insufficient funds");
            }

            // 3. Дебет со счёта-отправителя
            accountClient.debit(req.getFromAccountId(), req.getAmount());

            // 4. Кредит на счёт-получатель
            accountClient.credit(req.getToAccountId(), req.getAmount());

            // 5. Обновляем статус на COMPLETED
            tx.setStatus("COMPLETED");

        } catch (Exception ex) {
            // В случае ошибки помечаем транзакцию как FAILED
            tx.setStatus("FAILED");
            System.err.println("Transaction failed: " + ex.getMessage());
        }

        // 6. Сохраняем финальный статус
        tx = txRepo.save(tx);
        return new TransactionDto(tx);
    }

    public List<TransactionDto> getTransactionsByAccount(Long accountId) {
        List<Transaction> list = txRepo.findByFromAccountIdOrToAccountId(accountId, accountId);
        return list.stream()
                .map(TransactionDto::new)
                .collect(Collectors.toList());
    }
}
