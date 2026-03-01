package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.CoreClient;
import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.dtos.PagedResponse;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

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
        return coreClient.closeAccount(clientId, accountId);
    }

    public AccountDto deposit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.deposit( clientId, accountId, request);
    }

    public AccountDto withdraw(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        authService.validateTokenForClient(token, clientId);
        return coreClient.withdraw(clientId, accountId, request);
    }

    public List<AccountDto> getByClient(String token, UUID clientId) {
        authService.validateClientOrEmployee(token, clientId);
        return coreClient.getByClient(clientId);
    }

    public AccountDto getByIdCredit(String token, UUID clientId, UUID accountId, Role role) {
        authService.validateClientOrEmployee(token, clientId);
        return coreClient.getByIdCredit(clientId, accountId, role);
    }

    public AccountDto getById(String token, UUID clientId, UUID accountId, Role role) {
        authService.validateClientOrEmployee(token, clientId);
        return coreClient.getById(clientId, accountId, role);
    }

    public Page<AccountOperationResponse> operations(String token, UUID clientId, UUID accountId, Pageable pageable, AccountType type) {
        authService.validateClientOrEmployee(token, clientId);
        PagedResponse<AccountOperationResponse> response = coreClient.operations(clientId, accountId, pageable, Role.CLIENT, type);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllDebit(String token, Pageable pageable){
        authService.validateEmployeeByToken(token);
        PagedResponse<AccountDto> response = coreClient.getAllDebit(pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllCredit(String token, Pageable pageable){
        authService.validateEmployeeByToken(token);
        PagedResponse<AccountDto> response = coreClient.getAllCredit(pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public AccountDto getMyCreditAccount(String token, UUID clientId){
        authService.validateTokenForClient(token, clientId);
        return coreClient.getMyCreditAccount(clientId);
    }

    public AccountDto withdrawCredit( String token, UUID clientId,
                                     UUID accountId, MoneyRequest request){
        authService.validateTokenForClient(token, clientId);
        return coreClient.withdrawCredit(clientId, accountId, request);
    }

    public Page<AccountOperationResponse> operationsCredit(String token, UUID clientId,
                                                           UUID accountId, Pageable pageable){
        authService.validateTokenForClient(token, clientId);
        PagedResponse<AccountOperationResponse> response = coreClient.operationsCredit(clientId, accountId, pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }
}
