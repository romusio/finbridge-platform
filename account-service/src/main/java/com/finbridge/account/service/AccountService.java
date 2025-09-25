package com.finbridge.account.service;

import com.finbridge.account.dto.AccountDto;
import com.finbridge.account.dto.CreateAccountRequest;
import com.finbridge.account.entity.Account;
import com.finbridge.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepo;

    public AccountDto createAccount(CreateAccountRequest request) {
        Account account = new Account();
        account.setUserId(request.getUserId());
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency() != null ? request.getCurrency() : "RUB");
        account.setBalance(BigDecimal.ZERO);
        account.setActive(true);
        account.setCreatedAt(LocalDateTime.now());
        account.setUpdatedAt(LocalDateTime.now());
        account.setAccountNumber("ACC" + System.currentTimeMillis());
        Account saved = accountRepo.save(account);
        return new AccountDto(saved);
    }

    public List<AccountDto> getAccountsByUserId(Long userId) {
        return accountRepo.findByUserId(userId).stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }

    public BigDecimal getBalance(Long accountId) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return account.getBalance();
    }

    @Transactional
    public void debit(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient funds");
        }
        account.setBalance(account.getBalance().subtract(amount));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(account);
    }

    @Transactional
    public void credit(Long accountId, BigDecimal amount) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        account.setBalance(account.getBalance().add(amount));
        account.setUpdatedAt(LocalDateTime.now());
        accountRepo.save(account);
    }
    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepo.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return new AccountDto(account);
    }
}
