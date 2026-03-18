package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.CreateMasterAccountRequest;
import com.OnlineBankingService.dto.MasterAccountResponse;
import com.OnlineBankingService.dto.MoneyRequest;
import com.OnlineBankingService.service.MasterAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/master-account")
public class MasterAccountController {

    private final MasterAccountService masterAccountService;

    @PostMapping
    public MasterAccountResponse create(@Valid @RequestBody CreateMasterAccountRequest request) {
        return masterAccountService.create(request.initialBalance());
    }

    @GetMapping
    public MasterAccountResponse get() {
        return masterAccountService.get();
    }

    @PostMapping("/deposit")
    public MasterAccountResponse deposit(@Valid @RequestBody MoneyRequest request) {
        return masterAccountService.deposit(request.amount());
    }

    @PostMapping("/withdraw")
    public MasterAccountResponse withdraw(@Valid @RequestBody MoneyRequest request) {
        return masterAccountService.withdraw(request.amount());
    }

    @PostMapping("/internal/deposit")
    public MasterAccountResponse internalDeposit(@Valid @RequestBody MoneyRequest request) {
        return masterAccountService.deposit(request.amount());
    }
}