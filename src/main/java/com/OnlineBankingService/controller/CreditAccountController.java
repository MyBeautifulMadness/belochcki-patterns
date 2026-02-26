package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.AccountOperationResponse;
import com.OnlineBankingService.dto.CreditAccountCreateRequest;
import com.OnlineBankingService.dto.CreditAccountResponse;
import com.OnlineBankingService.dto.MoneyRequest;
import com.OnlineBankingService.service.CreditAccountService;
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
public class CreditAccountController {

    private final CreditAccountService service;

    @PostMapping("/credit-accounts")
    public CreditAccountResponse open(@Valid @RequestBody CreditAccountCreateRequest request) {
        return service.open(request);
    }

    @PostMapping("/credit-accounts/{accountId}/deposit")
    public CreditAccountResponse deposit(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.deposit(accountId, request);
    }

    @PostMapping("/credit-accounts/{accountId}/withdraw")
    public CreditAccountResponse withdraw(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.withdraw(accountId, request);
    }

    @PostMapping("/credit-accounts/{accountId}/close")
    public CreditAccountResponse close(@PathVariable UUID accountId) {
        return service.close(accountId);
    }

    @GetMapping("/clients/{clientId}/credit-accounts")
    public List<CreditAccountResponse> getByClient(@PathVariable Long clientId) {
        return service.getByClient(clientId);
    }

    @GetMapping("/credit-accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@PathVariable UUID accountId, Pageable pageable) {
        return service.getOperations(accountId, pageable);
    }
}
