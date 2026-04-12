package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.AuthClient;
import com.OnlineBankingService.configs.CoreClient;
import com.OnlineBankingService.configs.RetryExecutor;
import com.OnlineBankingService.dtos.*;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.UUID;

@Service
public class CoreGatewayService {

    private final CoreClient coreClient;
    private final RetryExecutor retryExecutor;

    public CoreGatewayService(CoreClient coreClient, AuthClient authClient, RetryExecutor retryExecutor) {
        this.coreClient = coreClient;
        this.retryExecutor = retryExecutor;
    }

    public AccountDto openAccount(UUID clientId, OpenDebitAccountRequest request) {
        return retryExecutor.execute(
                () -> coreClient.open(clientId, request),
                "gateway -> core open debit account"
        );
    }

    public AccountDto closeAccount(String token, UUID clientId, UUID accountId) {
        return retryExecutor.execute(
                () -> coreClient.closeAccount(clientId, accountId),
                "gateway -> core close debit account"
        );
    }

    public AccountDto deposit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.deposit(clientId, accountId, request),
                "gateway -> core deposit"
        );
    }

    public AccountDto withdraw(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.withdraw(clientId, accountId, request),
                "gateway -> core withdraw"
        );
    }

    public List<AccountDto> getByClient(String token, UUID clientId) {
        return retryExecutor.execute(
                () -> coreClient.getByClient(clientId),
                "gateway -> core get debit accounts by client"
        );
    }

    public AccountDto getByIdCredit(String token, UUID clientId, UUID accountId, Role role) {
        return retryExecutor.execute(
                () -> coreClient.getByIdCredit(clientId, accountId, role),
                "gateway -> core get credit account by id"
        );
    }

    public AccountDto getById(String token, UUID clientId, UUID accountId, Role role) {
        return retryExecutor.execute(
                () -> coreClient.getById(clientId, accountId, role),
                "gateway -> core get debit account by id"
        );
    }

    public Page<AccountOperationResponse> operations(String token, UUID clientId, UUID accountId, Pageable pageable, AccountType type) {
        PagedResponse<AccountOperationResponse> response = retryExecutor.execute(
                () -> coreClient.operations(clientId, accountId, pageable, Role.CLIENT, type),
                "gateway -> core account operations"
        );

        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllDebit(String token, Pageable pageable) {
        PagedResponse<AccountDto> response = retryExecutor.execute(
                () -> coreClient.getAllDebit(pageable),
                "gateway -> core get all debit accounts"
        );

        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllCredit(String token, Pageable pageable) {
        PagedResponse<AccountDto> response = retryExecutor.execute(
                () -> coreClient.getAllCredit(pageable),
                "gateway -> core get all credit accounts"
        );

        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public AccountDto getMyCreditAccount(String token, UUID clientId) {
        return retryExecutor.execute(
                () -> coreClient.getMyCreditAccount(clientId),
                "gateway -> core get my credit account"
        );
    }

    public AccountDto withdrawCredit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.withdrawCredit(clientId, accountId, request),
                "gateway -> core withdraw from credit account"
        );
    }

    public Page<AccountOperationResponse> operationsCredit(String token, UUID clientId, UUID accountId, Pageable pageable) {
        PagedResponse<AccountOperationResponse> response = retryExecutor.execute(
                () -> coreClient.operationsCredit(clientId, accountId, pageable),
                "gateway -> core credit account operations"
        );

        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public TransferResponse transfer(@PathVariable UUID clientId, @Valid @RequestBody TransferRequest request) {
        return retryExecutor.execute(
                () -> coreClient.transfer(clientId, request),
                "gateway -> core transfer"
        );
    }

    public List<CurrencyResponse> getAll() {
        return retryExecutor.execute(
                coreClient::getAll,
                "gateway -> core get currencies"
        );
    }

    public CurrencyResponse create(@Valid @RequestBody CreateCurrencyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.create(request),
                "gateway -> core create currency"
        );
    }

    public CurrencyResponse deactivate(@PathVariable String code) {
        return retryExecutor.execute(
                () -> coreClient.deactivate(code),
                "gateway -> core deactivate currency"
        );
    }

    public MasterAccountResponse createMaster(@Valid @RequestBody CreateMasterAccountRequest request) {
        return retryExecutor.execute(
                () -> coreClient.createMaster(request),
                "gateway -> core create master account"
        );
    }

    public MasterAccountResponse get() {
        return retryExecutor.execute(
                coreClient::get,
                "gateway -> core get master account"
        );
    }

    public MasterAccountResponse depositMaster(@Valid @RequestBody MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.depositMaster(request),
                "gateway -> core deposit master account"
        );
    }

    public MasterAccountResponse withdrawMaster(@Valid @RequestBody MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.withdrawMaster(request),
                "gateway -> core withdraw master account"
        );
    }

    public MasterAccountResponse internalDeposit(@Valid @RequestBody MoneyRequest request) {
        return retryExecutor.execute(
                () -> coreClient.internalDeposit(request),
                "gateway -> core internal deposit to master account"
        );
    }
}
