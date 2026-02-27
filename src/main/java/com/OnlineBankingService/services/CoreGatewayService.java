package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.CoreClient;
import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

@Service
public class CoreGatewayService {

    private final CoreClient coreClient;
    private final AuthService authService;

    public CoreGatewayService(CoreClient coreClient, AuthService authService) {
        this.coreClient = coreClient;
        this.authService = authService;
    }

    public AccountDto openAccount(String token, UUID clientId) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.openAccount(clientId);
    }

    public AccountDto closeAccount(String token, UUID clientId, UUID accountId) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.closeAccount(accountId, clientId);
    }

    public AccountDto deposit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.deposit(accountId, request, clientId);
    }

    public AccountDto withdraw(String token, UUID clientId, UUID accountId, MoneyRequest request, AccountType type) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.withdraw(accountId, request, clientId, type);
    }

    public List<AccountDto> getByClient(String token, UUID clientId, AccountType type) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.getByClient(clientId, type);
    }

    public Page<AccountOperationResponse> operations(String token, UUID clientId, UUID accountId, Pageable pageable, AccountType type) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.operations(accountId, pageable, clientId, Role.CLIENT, type);
    }

    public Page<AccountOperationResponse> employeeOperations(String token, UUID accountId, Pageable pageable, AccountType type) {
        authService.validateEmployeeByToken(token);
        return coreClient.operations(accountId, pageable, UUID.randomUUID(), Role.EMPLOYEE, type);
    }

    public Page<AccountDto> getAll(String token, Pageable pageable){
        authService.validateEmployeeByToken(token);
        return coreClient.getAll(pageable);
    }
}
