package com.finbridge.account.controller;

import com.finbridge.account.dto.AccountDto;
import com.finbridge.account.dto.CreateAccountRequest;
import com.finbridge.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account Management", description = "Bank account creation and management")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check if Account Service is running")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "FinBridge Account Service");
        response.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }

    @PostMapping
    @Operation(summary = "Create new account", description = "Create a new bank account for user")
    public ResponseEntity<?> createAccount(@Valid @RequestBody CreateAccountRequest request) {
        try {
            AccountDto account = accountService.createAccount(request);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account created successfully");
            response.put("account", account);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping
    @Operation(summary = "Get all accounts", description = "Get all accounts")
    public ResponseEntity<?> getAllAccounts() {
        List<AccountDto> accounts = accountService.getAllAccounts();
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user accounts", description = "Get all active accounts for a specific user")
    public ResponseEntity<?> getUserAccounts(@PathVariable("userId") Long userId) {
        try {
            List<AccountDto> accounts = accountService.getUserAccounts(userId);
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/{accountId}")
    @Operation(summary = "Get account by ID", description = "Get account details by account ID")
    public ResponseEntity<?> getAccount(@PathVariable("accountId") Long accountId) {
        try {
            AccountDto account = accountService.getAccountById(accountId);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @GetMapping("/number/{accountNumber}")
    @Operation(summary = "Get account by number", description = "Get account details by account number")
    public ResponseEntity<?> getAccountByNumber(@PathVariable("accountNumber") String accountNumber) {
        try {
            AccountDto account = accountService.getAccountByNumber(accountNumber);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{accountId}/balance")
    @Operation(summary = "Update account balance", description = "Update balance for specific account")
    public ResponseEntity<?> updateBalance(
            @PathVariable("accountId") Long accountId,
            @RequestBody Map<String, BigDecimal> request) {
        try {
            BigDecimal newBalance = request.get("balance");
            AccountDto account = accountService.updateBalance(accountId, newBalance);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Balance updated successfully");
            response.put("account", account);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/{accountId}")
    @Operation(summary = "Deactivate account", description = "Deactivate account (soft delete)")
    public ResponseEntity<?> deactivateAccount(@PathVariable("accountId") Long accountId) {
        try {
            AccountDto account = accountService.deactivateAccount(accountId);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Account deactivated successfully");
            response.put("account", account);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}
