package com.OnlineBankingService.controller;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.kafka.command.DepositCommand;
import com.OnlineBankingService.kafka.command.TransferCommand;
import com.OnlineBankingService.kafka.command.WithdrawCommand;
import com.OnlineBankingService.kafka.producer.AccountCommandProducer;
import com.OnlineBankingService.service.DebitAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core")
public class DebitAccountController {
    private final DebitAccountService service;
    private final AccountCommandProducer accountCommandProducer;

    @GetMapping("/clients/{clientId}/debit-accounts/{accountId}")
    public DebitAccountResponse getById(@PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role) {
        return service.getById(clientId, accountId, role);
    }

    @PostMapping("/clients/{clientId}/debit-accounts")
    public DebitAccountResponse open(@PathVariable UUID clientId, @Valid @RequestBody OpenDebitAccountRequest request) {
        return service.open(clientId, request);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/deposit")
    public ResponseEntity<Void> deposit(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        DepositCommand command = new DepositCommand(
                java.util.UUID.randomUUID(),
                clientId,
                accountId,
                request.amount(),
                request.comment()
        );

        accountCommandProducer.sendDeposit(command);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/withdraw")
    public ResponseEntity<Void> withdraw(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request) {
        WithdrawCommand command = new WithdrawCommand(
                java.util.UUID.randomUUID(),
                clientId,
                accountId,
                request.amount(),
                request.comment()
        );

        accountCommandProducer.sendWithdraw(command);
        return ResponseEntity.accepted().build();
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

    @PostMapping("/clients/{clientId}/debit-accounts/transfer")
    public ResponseEntity<Void> transfer(@PathVariable UUID clientId, @Valid @RequestBody TransferRequest request) {
        TransferCommand command = new TransferCommand(
                UUID.randomUUID(),
                clientId,
                request.fromAccountId(),
                request.toAccountId(),
                request.amount(),
                request.comment()
        );

        accountCommandProducer.sendTransfer(command);
        return ResponseEntity.accepted().build();
    }
}
