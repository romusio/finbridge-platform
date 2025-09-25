package com.finbridge.account.controller;

import com.finbridge.account.dto.AccountDto;
import com.finbridge.account.dto.CreateAccountRequest;
import com.finbridge.account.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/accounts")
@Tag(name = "Account", description = "Управление банковскими счетами")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Создать счёт",
            description = "Создаёт новый счёт для пользователя и возвращает DTO счёта"
    )
    @ApiResponse(
            responseCode = "200",
            description = "Счёт успешно создан",
            content = @Content(schema = @Schema(implementation = AccountDto.class))
    )
    @PostMapping
    public ResponseEntity<AccountDto> createAccount(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Параметры для создания счёта",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CreateAccountRequest.class))
            )
            @RequestBody CreateAccountRequest request
    ) {
        return ResponseEntity.ok(accountService.createAccount(request));
    }

    @Operation(summary = "Получить все счета пользователя",
            description = "Возвращает список DTO счетов по указанному userId")
    @ApiResponse(responseCode = "200", description = "Список счетов",
            content = @Content(schema = @Schema(implementation = AccountDto.class)))
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AccountDto>> getUserAccounts(
            @Parameter(description = "ID пользователя", example = "1")
            @PathVariable("userId") Long userId
    ) {
        return ResponseEntity.ok(accountService.getAccountsByUserId(userId));
    }

    @Operation(summary = "Получить баланс по счёту",
            description = "Возвращает текущий баланс счёта")
    @ApiResponse(responseCode = "200", description = "Баланс счёта",
            content = @Content(schema = @Schema(implementation = BigDecimal.class)))
    @GetMapping("/{id}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @Parameter(description = "ID счёта", example = "42")
            @PathVariable("id") Long accountId
    ) {
        return ResponseEntity.ok(accountService.getBalance(accountId));
    }

    @Operation(summary = "Дебетовать счёт",
            description = "Списывает указанную сумму с баланса счёта")
    @ApiResponse(responseCode = "200", description = "Списание успешно")
    @PostMapping("/{id}/debit")
    public ResponseEntity<Void> debit(
            @Parameter(description = "ID счёта", example = "42")
            @PathVariable("id") Long accountId,
            @Parameter(description = "Сумма списания", example = "100.00")
            @RequestParam("amount") BigDecimal amount
    ) {
        accountService.debit(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Кредитовать счёт",
            description = "Зачисляет указанную сумму на баланс счёта")
    @ApiResponse(responseCode = "200", description = "Зачисление успешно")
    @PostMapping("/{id}/credit")
    public ResponseEntity<Void> credit(
            @Parameter(description = "ID счёта", example = "42")
            @PathVariable("id") Long accountId,
            @Parameter(description = "Сумма зачисления", example = "200.00")
            @RequestParam("amount") BigDecimal amount
    ) {
        accountService.credit(accountId, amount);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Получить DTO счёта по ID",
            description = "Возвращает все поля счёта в формате AccountDto")
    @ApiResponse(responseCode = "200", description = "DTO счёта",
            content = @Content(schema = @Schema(implementation = AccountDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<AccountDto> getAccountById(
            @Parameter(description = "ID счёта", example = "42")
            @PathVariable("id") Long accountId
    ) {
        return ResponseEntity.ok(accountService.getAccountById(accountId));
    }
}
