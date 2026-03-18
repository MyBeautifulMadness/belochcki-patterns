package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.domain.AccountStatus;
import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.domain.OperationType;
import com.OnlineBankingService.domain.Role;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.entity.AccountOperation;
import com.OnlineBankingService.entity.DebitAccount;
import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.ForbiddenException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.generator.AccountNameGenerator;
import com.OnlineBankingService.repository.AccountOperationRepository;
import com.OnlineBankingService.repository.CreditAccountRepository;
import com.OnlineBankingService.repository.DebitAccountRepository;
import com.OnlineBankingService.service.CurrencyService;
import com.OnlineBankingService.service.DebitAccountService;
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
public class DebitAccountServiceImpl implements DebitAccountService {

    private final DebitAccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;
    private final CreditAccountRepository creditAccountRepository;
    private final AccountNameGenerator nameGenerator;
    private final CurrencyService currencyService;

    @Override
    @Transactional
    public DebitAccountResponse open(UUID clientId, OpenDebitAccountRequest request) {
        var nowDate = LocalDate.now();
        var nowTime = LocalTime.now().withNano(0);

        String currencyCode = request.currencyCode().toUpperCase();
        currencyService.getActiveCurrencyOrThrow(currencyCode);

        var account = DebitAccount.builder()
                .clientId(clientId)
                .createdDate(nowDate)
                .createdTime(nowTime)
                .balance(BigDecimal.ZERO.setScale(2))
                .name(generateUniqueNameForDebit())
                .currencyCode(currencyCode)
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

    private String generateUniqueNameForDebit() {
        for (int i = 0; i < 10; i++) {
            String candidate = nameGenerator.generate16Digits();
            if (!accountRepository.existsByName(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate unique account name");
    }

    @Override
    @Transactional(readOnly = true)
    public DebitAccountResponse getById(UUID clientId, UUID accountId, Role role) {
        if (role == Role.EMPLOYEE) {
            var acc = accountRepository.findById(accountId)
                    .orElseThrow(() -> new NotFoundException("Debit account not found: " + accountId));
            return toResponse(acc);
        }

        ensureAccountAccessible(clientId, accountId);
        var acc = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Debit account not found: " + accountId));
        return toResponse(acc);
    }

    @Override
    @Transactional
    public DebitAccountResponse deposit(UUID accountId, MoneyRequest request, UUID clientId) {
        BigDecimal delta = normalizeAmount(request.amount());

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(accountId, clientId, delta);
        if (newBalance == null) {
            ensureAccountAccessible(clientId, accountId);
            throw new ConflictException("Account is closed");
        }

        saveOperation(accountId, delta, request.comment(), OperationType.DEPOSIT);

        var account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional
    public DebitAccountResponse withdraw(UUID accountId, MoneyRequest request, UUID clientId) {
        BigDecimal delta = normalizeAmount(request.amount()).negate();

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(accountId, clientId, delta);
        if (newBalance == null) {
            ensureAccountAccessible(clientId, accountId);
            throw new ConflictException("Insufficient funds or account is closed");
        }

        saveOperation(accountId, delta.abs(), request.comment(), OperationType.WITHDRAW);

        var account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional
    public DebitAccountResponse close(UUID accountId, UUID clientId) {
        UUID closedId = accountRepository.closeIfZeroBalance(accountId, clientId);
        if (closedId == null) {
            ensureAccountAccessible(clientId, accountId);
            throw new ConflictException("Account must be OPEN and have zero balance to close");
        }

        saveOperation(accountId, BigDecimal.ZERO.setScale(2), "Account closed", OperationType.CLOSE);

        var account = accountRepository.findById(accountId).orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebitAccountResponse> getByClient(UUID clientId) {
        return accountRepository.findByClientId(clientId).stream()
                .map(this::toResponse)
                .toList();
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


    @Override
    @Transactional(readOnly = true)
    public Page<AccountOperationResponse> getOperations(UUID accountId, Pageable pageable, UUID clientId, Role role, AccountType accountType) {

        if (role == Role.EMPLOYEE) {
            ensureAccountExistsByType(accountId, accountType);
        } else {
            ensureAccountAccessibleByType(clientId, accountId, accountType);
        }

        return operationRepository.findByAccountId(accountId, pageable)
                .map(this::toResponse);
    }

    private void ensureAccountExistsByType(UUID accountId, AccountType accountType) {
        boolean exists = switch (accountType) {
            case DEBIT -> accountRepository.existsById(accountId);
            case CREDIT -> creditAccountRepository.existsById(accountId);
        };
        if (!exists) {
            throw new NotFoundException(accountType + " account not found: " + accountId);
        }
    }

    private void ensureAccountAccessibleByType(UUID clientId, UUID accountId, AccountType accountType) {
        boolean owned = switch (accountType) {
            case DEBIT -> accountRepository.existsByIdAndClientId(accountId, clientId);
            case CREDIT -> creditAccountRepository.existsByIdAndClientId(accountId, clientId);
        };

        if (owned) return;

        boolean exists = switch (accountType) {
            case DEBIT -> accountRepository.existsById(accountId);
            case CREDIT -> creditAccountRepository.existsById(accountId);
        };

        if (exists) {
            throw new ForbiddenException("You cannot access this account");
        }
        throw new NotFoundException(accountType + " account not found: " + accountId);
    }

    @Override
    @Transactional
    public DebitAccountResponse withdrawByCreditService(UUID debitAccountId, CreditServiceWithdrawRequest request, UUID clientId) {
        BigDecimal delta = normalizeAmount(request.amount()).negate();

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(debitAccountId, clientId, delta);
        if (newBalance == null) {
            ensureAccountAccessible(debitAccountId, clientId);
            throw new ConflictException("Insufficient funds or account is closed");
        }

        saveOperation(
                debitAccountId,
                delta.abs(),
                request.comment() != null ? request.comment() : "Withdrawal requested by Credit Service",
                OperationType.CREDIT_SERVICE_WITHDRAW
        );

        var account = accountRepository.findById(debitAccountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));
        return toResponse(account);
    }

    private void ensureAccountExists(UUID accountId) {
        if (!accountRepository.existsById(accountId)) {
            throw new NotFoundException("Account not found: " + accountId);
        }
    }

    private void ensureAccountAccessible(UUID clientId, UUID accountId) {
        if (accountRepository.existsByIdAndClientId(accountId, clientId)) {
            return;
        }
        if (accountRepository.existsById(accountId)) {
            throw new ForbiddenException("You cannot access this account");
        }
        throw new NotFoundException("Account not found: " + accountId);
    }

    private BigDecimal normalizeAmount(BigDecimal amount) {
        return amount.setScale(2);
    }

    private DebitAccountResponse toResponse(DebitAccount a) {
        return new DebitAccountResponse(
                a.getId(),
                a.getClientId(),
                a.getCreatedDate(),
                a.getCreatedTime(),
                a.getBalance(),
                a.getName(),
                a.getCurrencyCode(),
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
