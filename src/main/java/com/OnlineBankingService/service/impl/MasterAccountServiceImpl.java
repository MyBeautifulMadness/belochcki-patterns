package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.dto.MasterAccountResponse;
import com.OnlineBankingService.entity.AccountOperation;
import com.OnlineBankingService.entity.MasterAccount;
import com.OnlineBankingService.domain.AccountStatus;
import com.OnlineBankingService.domain.OperationType;
import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.generator.AccountNameGenerator;
import com.OnlineBankingService.kafka.command.MasterAccountDepositCommand;
import com.OnlineBankingService.kafka.command.MasterAccountWithdrawCommand;
import com.OnlineBankingService.repository.AccountOperationRepository;
import com.OnlineBankingService.repository.MasterAccountRepository;
import com.OnlineBankingService.service.MasterAccountService;
import com.OnlineBankingService.service.ws.AccountOperationWsPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MasterAccountServiceImpl implements MasterAccountService {

    private final MasterAccountRepository masterAccountRepository;
    private final AccountOperationRepository operationRepository;
    private final AccountNameGenerator nameGenerator;
    private final AccountOperationWsPublisher wsPublisher;

    @Override
    @Transactional
    public MasterAccountResponse create(BigDecimal initialBalance) {
        if (masterAccountRepository.count() > 0) {
            throw new ConflictException("Master account already exists");
        }

        var nowDate = LocalDate.now();
        var nowTime = LocalTime.now().withNano(0);

        MasterAccount account = MasterAccount.builder()
                .createdDate(nowDate)
                .createdTime(nowTime)
                .balance(initialBalance.setScale(2))
                .name(generateUniqueName())
                .currencyCode("RUB")
                .status(AccountStatus.OPEN)
                .build();

        account = masterAccountRepository.save(account);

        saveOperation(account.getId(), initialBalance, "Master account created", OperationType.OPEN);

        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public MasterAccountResponse get() {
        MasterAccount account = masterAccountRepository.findSingleMasterAccount()
                .orElseThrow(() -> new NotFoundException("Master account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional
    public void withdrawForCredit(BigDecimal amount) {
        MasterAccount account = masterAccountRepository.findSingleMasterAccount()
                .orElseThrow(() -> new NotFoundException("Master account not found"));

        if (account.getStatus() != AccountStatus.OPEN) {
            throw new ConflictException("Master account is closed");
        }

        BigDecimal normalizedAmount = amount.setScale(2);

        BigDecimal newBalance = masterAccountRepository.applyDeltaReturningBalance(
                account.getId(),
                normalizedAmount.negate()
        );

        if (newBalance == null) {
            throw new ConflictException("Insufficient funds on master account");
        }

        saveOperation(account.getId(), normalizedAmount, "Funds issued for credit", OperationType.TRANSFER_OUT);
    }

    @Override
    @Transactional
    public MasterAccountResponse deposit(BigDecimal amount) {
        MasterAccount account = masterAccountRepository.findSingleMasterAccount()
                .orElseThrow(() -> new NotFoundException("Master account not found"));

        BigDecimal normalizedAmount = amount.setScale(2);

        BigDecimal newBalance = masterAccountRepository.applyDeltaReturningBalance(
                account.getId(),
                normalizedAmount
        );

        if (newBalance == null) {
            throw new ConflictException("Failed to deposit to master account");
        }

        saveOperation(account.getId(), normalizedAmount, "Funds deposited to master account", OperationType.TRANSFER_IN);

        account.setBalance(newBalance);
        return toResponse(account);
    }

    @Override
    @Transactional
    public MasterAccountResponse withdraw(BigDecimal amount) {
        MasterAccount account = masterAccountRepository.findSingleMasterAccount()
                .orElseThrow(() -> new NotFoundException("Master account not found"));

        if (account.getStatus() != AccountStatus.OPEN) {
            throw new ConflictException("Master account is closed");
        }

        BigDecimal normalizedAmount = amount.setScale(2);

        BigDecimal newBalance = masterAccountRepository.applyDeltaReturningBalance(
                account.getId(),
                normalizedAmount.negate()
        );

        if (newBalance == null) {
            throw new ConflictException("Insufficient funds on master account");
        }

        saveOperation(
                account.getId(),
                normalizedAmount,
                "Funds withdrawn from master account",
                OperationType.TRANSFER_OUT
        );

        account.setBalance(newBalance);
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public UUID getMasterAccountId() {
        return masterAccountRepository.findSingleMasterAccount()
                .map(MasterAccount::getId)
                .orElseThrow(() -> new NotFoundException("Master account not found"));
    }

    @Override
    @Transactional
    public void processDepositCommand(MasterAccountDepositCommand command) {
        deposit(command.amount());
    }

    @Override
    @Transactional
    public void processWithdrawCommand(MasterAccountWithdrawCommand command) {
        withdraw(command.amount());
    }

    private String generateUniqueName() {
        for (int i = 0; i < 10; i++) {
            String candidate = nameGenerator.generate16Digits();
            if (!masterAccountRepository.existsByName(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate unique master account name");
    }

    private void saveOperation(UUID accountId, BigDecimal amount, String comment, OperationType type) {
        var nowDate = LocalDate.now();
        var nowTime = LocalTime.now().withNano(0);

        operationRepository.save(AccountOperation.builder()
                .accountId(accountId)
                .date(nowDate)
                .time(nowTime)
                .amount(amount.setScale(2))
                .comment(comment)
                .operationType(type)
                .build());

        wsPublisher.notifyAccountOperationsChanged(accountId);
    }

    private MasterAccountResponse toResponse(MasterAccount a) {
        return new MasterAccountResponse(
                a.getId(),
                a.getCreatedDate(),
                a.getCreatedTime(),
                a.getBalance(),
                a.getName(),
                a.getCurrencyCode(),
                a.getStatus()
        );
    }
}