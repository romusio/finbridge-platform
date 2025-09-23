package com.finbridge.account.repository;

import com.finbridge.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findByUserIdAndActive(Long userId, Boolean active);

    Optional<Account> findByAccountNumberAndActive(String accountNumber, Boolean active);

    List<Account> findByUserId(Long userId);

    boolean existsByAccountNumber(String accountNumber);

    List<Account> findByAccountTypeAndActive(Account.AccountType accountType, Boolean active);
}
