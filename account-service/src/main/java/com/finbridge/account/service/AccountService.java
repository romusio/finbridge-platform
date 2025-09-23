package com.finbridge.account.service;

import java.util.Collections;
import com.finbridge.account.dto.AccountDto;
import com.finbridge.account.dto.CreateAccountRequest;
import com.finbridge.account.entity.Account;
import com.finbridge.account.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public AccountDto createAccount(CreateAccountRequest request) {
        // Генерируем уникальный номер счета
        String accountNumber = generateAccountNumber();

        // Создаем новый счет
        Account account = new Account();
        account.setAccountNumber(accountNumber);
        account.setUserId(request.getUserId());
        account.setAccountName(request.getAccountName());
        account.setAccountType(request.getAccountType());
        account.setBalance(BigDecimal.ZERO);
        account.setCurrency(request.getCurrency());
        account.setActive(true);

        Account savedAccount = accountRepository.save(account);
        return new AccountDto(savedAccount);
    }

    public List<AccountDto> getUserAccounts(Long userId) {
        // Логируем входящие данные
        System.out.println("getUserAccounts called with userId=" + userId);

        // Получаем список активных аккаунтов (может быть пустым)
        List<Account> accounts = accountRepository.findByUserIdAndActive(userId, true);
        System.out.println("Found accounts: " + accounts);

        // Если списка нет, возвращаем пустой список
        if (accounts == null || accounts.isEmpty()) {
            return Collections.emptyList();
        }

        // Иначе – преобразуем в DTO
        return accounts.stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }


    public AccountDto getAccountByNumber(String accountNumber) {
        Account account = accountRepository.findByAccountNumberAndActive(accountNumber, true)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountNumber));
        return new AccountDto(account);
    }

    public AccountDto getAccountById(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));
        return new AccountDto(account);
    }

    public AccountDto updateBalance(Long accountId, BigDecimal newBalance) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Balance cannot be negative");
        }

        account.setBalance(newBalance);
        Account savedAccount = accountRepository.save(account);
        return new AccountDto(savedAccount);
    }

    public AccountDto deactivateAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found: " + accountId));

        account.setActive(false);
        Account savedAccount = accountRepository.save(account);
        return new AccountDto(savedAccount);
    }

    private String generateAccountNumber() {
        // Генерируем номер счета формата: FB + текущая дата + случайные цифры
        String prefix = "FB";
        String datePart = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Random random = new Random();
        String randomPart = String.format("%04d", random.nextInt(10000));

        String accountNumber = prefix + datePart + randomPart;

        // Проверяем уникальность
        while (accountRepository.existsByAccountNumber(accountNumber)) {
            randomPart = String.format("%04d", random.nextInt(10000));
            accountNumber = prefix + datePart + randomPart;
        }

        return accountNumber;
    }
    public List<AccountDto> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(AccountDto::new)
                .collect(Collectors.toList());
    }
}
