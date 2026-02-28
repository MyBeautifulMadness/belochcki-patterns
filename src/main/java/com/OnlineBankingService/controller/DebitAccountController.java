package com.OnlineBankingService.controller;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.AccountOperationResponse;
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

    @GetMapping("/clients/{clientId}/debit-accounts/{accountId}")
    public DebitAccountResponse getById(@PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role) {
        return service.getById(clientId, accountId, role);
    }

    @PostMapping("/clients/{clientId}/debit-accounts")
    public DebitAccountResponse open(@PathVariable UUID clientId) {
        return service.open(clientId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/deposit")
    public DebitAccountResponse deposit(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.deposit(accountId, request, clientId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/withdraw")
    public DebitAccountResponse withdraw(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.withdraw(accountId, request, clientId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/close")
    public DebitAccountResponse close(@PathVariable UUID clientId, @PathVariable UUID accountId) {
        return service.close(accountId, clientId);
    }

    @GetMapping("/clients/{clientId}/debit-accounts")
    public List<DebitAccountResponse> getByClient(@PathVariable UUID clientId) {
        return service.getByClient(clientId);
    }

    @GetMapping("/clients/{clientId}/accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@PathVariable UUID clientId, @PathVariable UUID accountId,
                                                     Pageable pageable,
                                                     @RequestParam Role role, @RequestParam AccountType accountType) {
        return service.getOperations(accountId, pageable, clientId, role, accountType);
    }

}
