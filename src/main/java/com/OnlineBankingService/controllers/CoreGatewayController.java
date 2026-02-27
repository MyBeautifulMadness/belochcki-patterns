package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.services.CoreGatewayService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/gateway/accounts")
public class CoreGatewayController {

    private final CoreGatewayService gatewayService;

    public CoreGatewayController(CoreGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }


    @PostMapping("/debit-accounts/{clientId}")
    public AccountDto openAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID clientId
    ) {
        return gatewayService.openAccount(token, clientId);
    }

    @PostMapping("/debit-accounts/{accountId}/close/{clientId}")
    public AccountDto closeAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId
    ) {
        return gatewayService.closeAccount(token, clientId, accountId);
    }

    @PostMapping("/debit-accounts/{accountId}/deposit")
    public AccountDto deposit(@RequestHeader("Authorization") String token,
                              @PathVariable UUID clientId,
                              @PathVariable UUID accountId,
                              @Valid @RequestBody MoneyRequest request){
        return gatewayService.deposit(token, clientId, accountId, request);
    }

    @PostMapping("/accounts/{accountId}/withdraw")
    public AccountDto withdraw(@RequestHeader("Authorization") String token,
                               @PathVariable UUID clientId,
                               @PathVariable UUID accountId,
                               @Valid @RequestBody MoneyRequest request,
                               @RequestBody AccountType type){
        return gatewayService.withdraw(token, clientId, accountId, request, type);
    }

    @GetMapping("/clients/{clientId}/accounts")
    public List<AccountDto> getByClient(@RequestHeader("Authorization") String token, @PathVariable UUID clientId, @RequestBody AccountType type){
        return gatewayService.getByClient(token, clientId, type);
    }

    @GetMapping("/accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@RequestHeader("Authorization") String token,
                                                     @PathVariable UUID clientId,
                                                     @PathVariable UUID accountId,
                                                     @RequestBody Pageable pageable,
                                                     @RequestBody AccountType type){
        return gatewayService.operations(token, clientId, accountId, pageable, type);
    }

    @GetMapping("/accounts/{accountId}/operations/employee")
    public Page<AccountOperationResponse> employeeOperations(@RequestHeader("Authorization") String token,
                                                     @PathVariable UUID accountId,
                                                             @RequestBody Pageable pageable,
                                                             @RequestBody AccountType type){
        return gatewayService.employeeOperations(token, accountId, pageable, type);
    }

    @GetMapping("/accounts")
    public Page<AccountDto> getAll(@RequestHeader("Authorization") String token, @RequestBody Pageable pageable) {
        return gatewayService.getAll(token, pageable);
    }

}
