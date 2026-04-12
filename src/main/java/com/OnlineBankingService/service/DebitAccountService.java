package com.OnlineBankingService.service;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.kafka.command.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DebitAccountService {
    DebitAccountResponse open(UUID clientId, OpenDebitAccountRequest request);

    DebitAccountResponse deposit(UUID accountId, MoneyRequest request, UUID clientId, String idempotencyKey);

    DebitAccountResponse withdraw(UUID accountId, MoneyRequest request, UUID clientId, String idempotencyKey);

    DebitAccountResponse close(UUID clientId, UUID accountId);

    List<DebitAccountResponse> getByClient(UUID clientId);

    Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable, UUID clientId, Role role, AccountType accountType);

    DebitAccountResponse withdrawByCreditService(UUID debitAccountId, CreditServiceWithdrawRequest request, UUID clientId);

    DebitAccountResponse getById(UUID clientId, UUID accountId, Role role);

    TransferResponse transfer(UUID clientId, TransferRequest request, String idempotencyKey);

    void processDepositCommand(DepositCommand command);

    void processWithdrawCommand(WithdrawCommand command);

    void processTransferCommand(TransferCommand command);

    void processOpenDebitAccountCommand(OpenDebitAccountCommand command);

    void processCloseDebitAccountCommand(CloseDebitAccountCommand command);
}
