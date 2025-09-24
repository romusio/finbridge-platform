package com.finbridge.transaction.controller;

import com.finbridge.transaction.dto.TransactionDto;
import com.finbridge.transaction.dto.TransactionRequest;
import com.finbridge.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    @Autowired
    private TransactionService txService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TransactionRequest req) {
        try {
            TransactionDto dto = txService.createTransaction(req);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/account/{accountId}")
    public ResponseEntity<List<TransactionDto>> getByAccount(@PathVariable("accountId") Long accountId) {
        return ResponseEntity.ok(txService.getTransactionsByAccount(accountId));
    }
}
