package com.OnlineBankingService.service;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DebitAccountService {
    DebitAccountResponse open(UUID clientId, DebitAccountCreateRequest request);

    DebitAccountResponse deposit(UUID accountId, MoneyRequest request, UUID clientId);

    DebitAccountResponse withdraw(UUID accountId, MoneyRequest request, UUID clientId);

    DebitAccountResponse close(UUID accountId, UUID clientId);

    List<DebitAccountResponse> getByClient(UUID clientId);

    Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable, UUID clientId, Role role, AccountType accountType);

    DebitAccountResponse withdrawByCreditService(UUID debitAccountId, CreditServiceWithdrawRequest request, UUID clientId);
}
