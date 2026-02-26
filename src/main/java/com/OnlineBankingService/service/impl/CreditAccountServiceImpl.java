package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.domain.AccountStatus;
import com.OnlineBankingService.domain.OperationType;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.entity.AccountOperation;
import com.OnlineBankingService.entity.CreditAccount;
import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.repository.AccountOperationRepository;
import com.OnlineBankingService.repository.CreditAccountRepository;
import com.OnlineBankingService.service.CreditAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountServiceImpl implements CreditAccountService {

    private final CreditAccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;

    @Override
    @Transactional
    public CreditAccountResponse open(CreditAccountCreateRequest request) {
        var nowDate = LocalDate.now();
        var nowTime = LocalTime.now().withNano(0);

        var account = CreditAccount.builder()
                .clientId(request.clientId())
                .createdDate(nowDate)
                .createdTime(nowTime)
                .balance(BigDecimal.ZERO.setScale(2))
                .name(request.name())
                .status(AccountStatus.OPEN)
                .build();

        account = accountRepository.save(account);

        operationRepository.save(AccountOperation.builder()
                .accountId(account.getId())
                .date(nowDate)
                .time(nowTime)
                .amount(BigDecimal.ZERO.setScale(2))
                .comment("Account opened")
                .operationType(OperationType.OPEN)
                .build());

        return toResponse(account);
    }

    @Override
    @Transactional
    public CreditAccountResponse deposit(UUID accountId, MoneyRequest request) {
        BigDecimal delta = normalizeAmount(request.amount());

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(accountId, delta);
        if (newBalance == null) {
            ensureAccountExists(accountId);
            throw new ConflictException("Account is closed");
        }

        saveOperation(accountId, delta, request.comment(), OperationType.DEPOSIT);

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional
    public CreditAccountResponse withdraw(UUID accountId, MoneyRequest request) {
        BigDecimal delta = normalizeAmount(request.amount()).negate();

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(accountId, delta);
        if (newBalance == null) {
            ensureAccountExists(accountId);
            throw new ConflictException("Insufficient funds or account is closed");
        }

        saveOperation(accountId, delta.abs(), request.comment(), OperationType.WITHDRAW);

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional
    public CreditAccountResponse close(UUID accountId) {
        Long closedId = accountRepository.closeIfZeroBalance(accountId);
        if (closedId == null) {
            ensureAccountExists(accountId);
            throw new ConflictException("Account must be OPEN and have zero balance to close");
        }

        saveOperation(accountId, BigDecimal.ZERO.setScale(2), "Account closed", OperationType.CLOSE);

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CreditAccountResponse> getByClient(Long clientId) {
        return accountRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable) {
        ensureAccountExists(accountId);
        return operationRepository.findByAccountId(accountId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public CreditAccountResponse withdrawByCreditService(UUID creditAccountId, CreditServiceWithdrawRequest request) {
        BigDecimal delta = normalizeAmount(request.amount()).negate();

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(creditAccountId, delta);
        if (newBalance == null) {
            ensureAccountExists(creditAccountId);
            throw new ConflictException("Insufficient funds or account is closed");
        }

        saveOperation(
                creditAccountId,
                delta.abs(),
                request.comment() != null ? request.comment() : "Withdrawal requested by Credit Service",
                OperationType.CREDIT_SERVICE_WITHDRAW
        );

        var account = accountRepository.findById(creditAccountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
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
    }

    private void ensureAccountExists(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Account not found: " + accountId);
        }
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2);
    }

    private CreditAccountResponse toResponse(CreditAccount a) {
        return new CreditAccountResponse(
                a.getId(),
                a.getClientId(),
                a.getCreatedDate(),
                a.getCreatedTime(),
                a.getBalance(),
                a.getName(),
                a.getStatus()
        );
    }

    private AccountOperationResponse toResponse(AccountOperation o) {
        return new AccountOperationResponse(
                o.getId(),
                o.getAccountId(),
                o.getDate(),
                o.getTime(),
                o.getAmount(),
                o.getComment(),
                o.getOperationType()
        );
    }
}
