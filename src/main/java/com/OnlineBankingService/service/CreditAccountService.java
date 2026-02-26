package com.OnlineBankingService.service;


import com.OnlineBankingService.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CreditAccountService {

    CreditAccountResponse open(CreditAccountCreateRequest request);

    CreditAccountResponse deposit(UUID accountId, MoneyRequest request);

    CreditAccountResponse withdraw(UUID accountId, MoneyRequest request);

    CreditAccountResponse close(UUID accountId);

    List<CreditAccountResponse> getByClient(Long clientId);

    Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable);

    CreditAccountResponse withdrawByCreditService(UUID creditAccountId, CreditServiceWithdrawRequest request);
}
