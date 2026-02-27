package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.CoreClient;
import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.DebitAccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
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

    public DebitAccountDto openAccount(String token, UUID clientId) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.openAccount(clientId);
    }

    public DebitAccountDto closeAccount(String token, UUID clientId, UUID accountId) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.closeAccount(accountId, clientId);
    }

    public DebitAccountDto deposit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.deposit(accountId, request, clientId);
    }

    public DebitAccountDto withdraw(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.withdraw(accountId, request, clientId);
    }

    public List<DebitAccountDto> getByClient(String token, UUID clientId) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.getByClient(clientId);
    }

    public Page<AccountOperationResponse> operations(String token, UUID clientId, UUID accountId, Pageable pageable) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.operations(accountId, pageable, clientId, Role.CLIENT);
    }

    public Page<AccountOperationResponse> employeeOperations(String token, UUID accountId, Pageable pageable) {
        authService.validateEmployeeByToken(token);
        return coreClient.operations(accountId, pageable, UUID.randomUUID(), Role.EMPLOYEE);
    }

    public Page<DebitAccountDto> getAllDebit(String token, Pageable pageable){
        authService.validateEmployeeByToken(token);
        return coreClient.getAllDebit(pageable);
    }
}
