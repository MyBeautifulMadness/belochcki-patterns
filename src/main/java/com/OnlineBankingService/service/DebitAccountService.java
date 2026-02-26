package com.OnlineBankingService.service;

import com.OnlineBankingService.dto.AccountOperationResponse;
import com.OnlineBankingService.dto.DebitAccountCreateRequest;
import com.OnlineBankingService.dto.DebitAccountResponse;
import com.OnlineBankingService.dto.MoneyRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface DebitAccountService {
    DebitAccountResponse open(DebitAccountCreateRequest request);

    DebitAccountResponse deposit(UUID accountId, MoneyRequest request);

    DebitAccountResponse withdraw(UUID accountId, MoneyRequest request);

    DebitAccountResponse close(UUID accountId);

    List<DebitAccountResponse> getByClient(Long clientId);

    Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable);
}
