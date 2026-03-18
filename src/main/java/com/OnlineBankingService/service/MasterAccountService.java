package com.OnlineBankingService.service;

import com.OnlineBankingService.dto.MasterAccountResponse;

import java.math.BigDecimal;
import java.util.UUID;

public interface MasterAccountService {

    MasterAccountResponse create(BigDecimal initialBalance);

    MasterAccountResponse get();

    void withdrawForCredit(BigDecimal amount);

    MasterAccountResponse deposit(BigDecimal amount);

    MasterAccountResponse withdraw(BigDecimal amount);

    UUID getMasterAccountId();
}