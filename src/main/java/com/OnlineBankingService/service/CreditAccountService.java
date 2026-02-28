package com.OnlineBankingService.service;


import com.OnlineBankingService.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface CreditAccountService {

    CreditAccountResponse withdraw(UUID clientId, UUID creditAccountId, MoneyRequest request);

    CreditAccountResponse getByClient(UUID clientId);

    Page<AccountOperationResponse> getOperations(UUID clientId, UUID creditAccountId, Pageable pageable);

    CreditAccountResponse onCreditIssued(CreditIssuedRequest request);

    CreditAccountResponse closeByCreditService(UUID clientId);
}
