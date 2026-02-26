package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.AccountOperationResponse;
import com.OnlineBankingService.dto.DebitAccountCreateRequest;
import com.OnlineBankingService.dto.DebitAccountResponse;
import com.OnlineBankingService.dto.MoneyRequest;
import com.OnlineBankingService.service.DebitAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core")
public class DebitAccountController {
    private final DebitAccountService service;

    @PostMapping("/debit-accounts")
    public DebitAccountResponse open(@Valid @RequestBody DebitAccountCreateRequest request) {
        return service.open(request);
    }

    @PostMapping("/debit-accounts/{accountId}/deposit")
    public DebitAccountResponse deposit(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.deposit(accountId, request);
    }

    @PostMapping("/debit-accounts/{accountId}/withdraw")
    public DebitAccountResponse withdraw(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.withdraw(accountId, request);
    }

    @PostMapping("/debit-accounts/{accountId}/close")
    public DebitAccountResponse close(@PathVariable UUID accountId) {
        return service.close(accountId);
    }

    @GetMapping("/clients/{clientId}/debit-accounts")
    public List<DebitAccountResponse> getByClient(@PathVariable Long clientId) {
        return service.getByClient(clientId);
    }

    @GetMapping("/debit-accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@PathVariable UUID accountId, Pageable pageable) {
        return service.getOperations(accountId, pageable);
    }
}
