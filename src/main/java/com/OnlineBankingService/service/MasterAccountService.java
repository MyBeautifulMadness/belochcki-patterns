package com.OnlineBankingService.service;

import com.OnlineBankingService.dto.MasterAccountResponse;
import com.OnlineBankingService.kafka.command.MasterAccountDepositCommand;
import com.OnlineBankingService.kafka.command.MasterAccountWithdrawCommand;

import java.math.BigDecimal;
import java.util.UUID;

public interface MasterAccountService {

    MasterAccountResponse create(BigDecimal initialBalance);

    MasterAccountResponse get();

    void withdrawForCredit(BigDecimal amount);

    MasterAccountResponse deposit(BigDecimal amount);

    MasterAccountResponse withdraw(BigDecimal amount);

    UUID getMasterAccountId();

    void processDepositCommand(MasterAccountDepositCommand command);

    void processWithdrawCommand(MasterAccountWithdrawCommand command);
}