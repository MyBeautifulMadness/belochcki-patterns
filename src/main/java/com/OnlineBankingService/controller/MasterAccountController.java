package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.CreateMasterAccountRequest;
import com.OnlineBankingService.dto.MasterAccountResponse;
import com.OnlineBankingService.dto.MoneyRequest;
import com.OnlineBankingService.kafka.command.MasterAccountDepositCommand;
import com.OnlineBankingService.kafka.command.MasterAccountWithdrawCommand;
import com.OnlineBankingService.kafka.producer.AccountCommandProducer;
import com.OnlineBankingService.service.MasterAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/master-account")
public class MasterAccountController {

    private final MasterAccountService masterAccountService;
    private final AccountCommandProducer accountCommandProducer;

    @PostMapping
    public MasterAccountResponse create(@Valid @RequestBody CreateMasterAccountRequest request) {
        return masterAccountService.create(request.initialBalance());
    }

    @GetMapping
    public MasterAccountResponse get() {
        return masterAccountService.get();
    }

    @PostMapping("/deposit")
    public ResponseEntity<Void> deposit(@Valid @RequestBody MoneyRequest request) {
        MasterAccountDepositCommand command = new MasterAccountDepositCommand(
                UUID.randomUUID(),
                request.amount(),
                request.comment()
        );

        accountCommandProducer.sendMasterAccountDeposit(command);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Void> withdraw(@Valid @RequestBody MoneyRequest request) {
        MasterAccountWithdrawCommand command = new MasterAccountWithdrawCommand(
                UUID.randomUUID(),
                request.amount(),
                request.comment()
        );

        accountCommandProducer.sendMasterAccountWithdraw(command);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/internal/deposit")
    public MasterAccountResponse internalDeposit(@Valid @RequestBody MoneyRequest request) {
        return masterAccountService.deposit(request.amount());
    }
}