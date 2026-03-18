package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.AuthClient;
import com.OnlineBankingService.configs.CoreClient;
import com.OnlineBankingService.dtos.*;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CoreGatewayService {

    private final CoreClient coreClient;

    public CoreGatewayService(CoreClient coreClient, AuthClient authClient) {
        this.coreClient = coreClient;
    }

    public AccountDto openAccount(String token, UUID clientId) {
        return coreClient.openAccount(clientId);
    }

    public AccountDto closeAccount(String token, UUID clientId, UUID accountId) {
        return coreClient.closeAccount(clientId, accountId);
    }

    public AccountDto deposit(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        return coreClient.deposit( clientId, accountId, request);
    }

    public AccountDto withdraw(String token, UUID clientId, UUID accountId, MoneyRequest request) {
        return coreClient.withdraw(clientId, accountId, request);
    }

    public List<AccountDto> getByClient(String token, UUID clientId) {
        return coreClient.getByClient(clientId);
    }

    public AccountDto getByIdCredit(String token, UUID clientId, UUID accountId, Role role) {
        return coreClient.getByIdCredit(clientId, accountId, role);
    }

    public AccountDto getById(String token, UUID clientId, UUID accountId, Role role) {
        return coreClient.getById(clientId, accountId, role);
    }

    public Page<AccountOperationResponse> operations(String token, UUID clientId, UUID accountId, Pageable pageable, AccountType type) {
        PagedResponse<AccountOperationResponse> response = coreClient.operations(clientId, accountId, pageable, Role.CLIENT, type);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllDebit(String token, Pageable pageable){
        PagedResponse<AccountDto> response = coreClient.getAllDebit(pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public Page<AccountDto> getAllCredit(String token, Pageable pageable){
        PagedResponse<AccountDto> response = coreClient.getAllCredit(pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }

    public AccountDto getMyCreditAccount(String token, UUID clientId){
        return coreClient.getMyCreditAccount(clientId);
    }

    public AccountDto withdrawCredit( String token, UUID clientId,
                                     UUID accountId, MoneyRequest request){
        return coreClient.withdrawCredit(clientId, accountId, request);
    }

    public Page<AccountOperationResponse> operationsCredit(String token, UUID clientId,
                                                           UUID accountId, Pageable pageable){
        PagedResponse<AccountOperationResponse> response = coreClient.operationsCredit(clientId, accountId, pageable);
        return new PageImpl<>(
                response.getContent(),
                PageRequest.of(response.getPage(), response.getSize()),
                response.getTotalElements()
        );
    }
}
