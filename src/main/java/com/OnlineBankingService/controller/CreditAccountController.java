package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.*;
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

    @GetMapping("/clients/{clientId}/credit-account")
    public CreditAccountResponse getMyCreditAccount(@PathVariable UUID clientId) {
        return service.getByClient(clientId);
    }

    @PostMapping("/credit-accounts/{accountId}/withdraw")
    public CreditAccountResponse withdraw(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        return service.withdraw(clientId, accountId, request);
    }

    @GetMapping("/credit-accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@PathVariable UUID clientId, @PathVariable UUID accountId, Pageable pageable) {
        return service.getOperations(clientId, accountId, pageable);
    }

    @PostMapping("/credit-issued")
    public CreditAccountResponse onCreditIssued(@Valid @RequestBody CreditIssuedRequest request) {
        return service.onCreditIssued(request);
    }

    @PostMapping("/{clientId}/close")
    public CreditAccountResponse close(@PathVariable UUID clientId) {
        return service.closeByCreditService(clientId);
    }
}
