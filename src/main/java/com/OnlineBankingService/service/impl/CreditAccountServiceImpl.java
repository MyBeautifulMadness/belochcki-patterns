package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.domain.AccountStatus;
import com.OnlineBankingService.domain.OperationType;
import com.OnlineBankingService.dto.*;
import com.OnlineBankingService.entity.AccountOperation;
import com.OnlineBankingService.entity.CreditAccount;
import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.ForbiddenException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.generator.AccountNameGenerator;
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
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditAccountServiceImpl implements CreditAccountService {

    private final CreditAccountRepository accountRepository;
    private final AccountOperationRepository operationRepository;
    private final AccountNameGenerator nameGenerator;

    @Transactional
    public CreditAccountResponse withdraw(UUID clientId,
                                          UUID accountId,
                                          MoneyRequest request) {

        CreditAccount account = accountRepository.findById(accountId)
                .orElseThrow(() -> new NotFoundException("Account not found"));

        if (!account.getClientId().equals(clientId)) {
            throw new ForbiddenException("Access denied");
        }

        BigDecimal delta = request.amount().setScale(2).negate();

        BigDecimal newBalance = accountRepository.applyDeltaReturningBalance(accountId, delta);

        if (newBalance == null) {
            throw new ConflictException("Insufficient funds or account closed");
        }

        saveOperation(accountId, request.amount(),
                request.comment(), OperationType.WITHDRAW);

        account.setBalance(newBalance);
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public CreditAccountResponse getByClient(UUID clientId) {
        CreditAccount account = accountRepository.findByClientId(clientId)
                .orElseThrow(() -> new NotFoundException("Credit account not found for client: " + clientId));
        return toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AccountOperationResponse> getOperations(UUID clientId, UUID creditAccountId, Pageable pageable) {
        CreditAccount account = accountRepository.findById(creditAccountId)
                .orElseThrow(() -> new NotFoundException("Credit account not found: " + creditAccountId));

        if (!account.getClientId().equals(clientId)) {
            throw new ForbiddenException("You cannot access this credit account");
        }

        return operationRepository.findByAccountId(creditAccountId, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public CreditAccountResponse onCreditIssued(CreditIssuedRequest request) {
        UUID clientId = request.clientId();
        BigDecimal amount = normalizeAmount(request.amount());

        BigDecimal newBalance = accountRepository.addToBalanceByClientIdOpen(clientId, amount);

        if (newBalance != null) {
            CreditAccount acc = accountRepository.findByClientId(clientId)
                    .orElseThrow(() -> new NotFoundException("Credit account not found after top-up for client: " + clientId));

            saveOperation(acc.getId(), amount, request.commentOrDefault("Credit issued"), OperationType.DEPOSIT);
            acc.setBalance(newBalance);
            return toResponse(acc);
        }

        newBalance = accountRepository.openAndAddToBalanceByClientId(clientId, amount);
        if (newBalance != null) {
            CreditAccount acc = accountRepository.findByClientId(clientId)
                    .orElseThrow(() -> new NotFoundException("Credit account not found after reopen for client: " + clientId));

            saveOperation(acc.getId(), amount, request.commentOrDefault("Credit issued (reopen)"), OperationType.DEPOSIT);
            acc.setBalance(newBalance);
            return toResponse(acc);
        }

        var nowDate = LocalDate.now();
        var nowTime = LocalTime.now().withNano(0);

        CreditAccount created = CreditAccount.builder()
                .clientId(clientId)
                .createdDate(nowDate)
                .createdTime(nowTime)
                .balance(amount)
                .name(generateUniqueNameForCredit())
                .status(AccountStatus.OPEN)
                .build();

        created = accountRepository.save(created);

        saveOperation(created.getId(), amount, request.commentOrDefault("Credit issued (create)"), OperationType.DEPOSIT);

        return toResponse(created);
    }

    private String generateUniqueNameForCredit() {
        for (int i = 0; i < 10; i++) {
            String candidate = nameGenerator.generate16Digits();
            if (!accountRepository.existsByName(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException("Failed to generate unique account name");
    }

    @Override
    @Transactional
    public CreditAccountResponse closeByCreditService(UUID clientId) {

        CreditAccount account = accountRepository.findByClientId(clientId)
                .orElseThrow(() -> new NotFoundException("Credit account not found for client: " + clientId));

        int updated = accountRepository.closeByClientId(clientId);
        if (updated == 0) {
            return toResponse(account);
        }

        saveOperation(account.getId(), BigDecimal.ZERO.setScale(2), "Credit account closed by Credit Service", OperationType.CLOSE);

        account.setStatus(AccountStatus.CLOSED);
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
