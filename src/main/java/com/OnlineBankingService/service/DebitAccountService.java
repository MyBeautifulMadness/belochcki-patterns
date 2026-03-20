package com.OnlineBankingService.service;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.kafka.command.DepositCommand;
import com.OnlineBankingService.kafka.command.WithdrawCommand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DebitAccountService {
    DebitAccountResponse open(UUID clientId, OpenDebitAccountRequest request);

    DebitAccountResponse deposit(UUID accountId, MoneyRequest request, UUID clientId);

    DebitAccountResponse withdraw(UUID accountId, MoneyRequest request, UUID clientId);

    DebitAccountResponse close(UUID accountId, UUID clientId);

    List<DebitAccountResponse> getByClient(UUID clientId);

    Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable, UUID clientId, Role role, AccountType accountType);

    DebitAccountResponse withdrawByCreditService(UUID debitAccountId, CreditServiceWithdrawRequest request, UUID clientId);

    DebitAccountResponse getById(UUID clientId, UUID accountId, Role role);

    TransferResponse transfer(UUID clientId, TransferRequest request);

    void processDepositCommand(DepositCommand command);

    void processWithdrawCommand(WithdrawCommand command);

    void processTransferCommand(com.OnlineBankingService.kafka.command.TransferCommand command);
}
