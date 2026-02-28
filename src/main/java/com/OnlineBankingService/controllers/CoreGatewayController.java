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


    @PostMapping("/clients/{clientId}/debit-accounts")
    public AccountDto openAccount(
            @RequestBody String token,
            @PathVariable UUID clientId
    ) {
        return gatewayService.openAccount(token, clientId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/close")
    public AccountDto closeAccount(
            @RequestBody String token,
            @PathVariable UUID clientId, @PathVariable UUID accountId
    ) {
        return gatewayService.closeAccount(token, clientId, accountId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/deposit")
    public AccountDto deposit(@RequestBody String token,
                              @PathVariable UUID clientId, @PathVariable UUID accountId,
                              @Valid @RequestBody MoneyRequest request){
        return gatewayService.deposit(token, clientId, accountId, request);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/withdraw")
    public AccountDto withdraw(@RequestBody String token,
                               @PathVariable UUID clientId,
                               @PathVariable UUID accountId,
                               @Valid @RequestBody MoneyRequest request){
        return gatewayService.withdraw(token, clientId, accountId, request);
    }

    @GetMapping("/clients/{clientId}/debit-accounts")
    public List<AccountDto> getByClient(@RequestBody String token, @PathVariable UUID clientId){
        return gatewayService.getByClient(token, clientId);
    }

    @GetMapping("/clients/{clientId}/accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@RequestBody String token,
                                                     @PathVariable UUID clientId, @PathVariable UUID accountId,
                                                     Pageable pageable,
                                                     @RequestParam AccountType accountType){
        return gatewayService.operations(token, clientId, accountId, pageable, accountType);
    }

    @GetMapping("/accounts/{accountId}/operations/employee")
    public Page<AccountOperationResponse> employeeOperations(@RequestBody String token,
                                                     @PathVariable UUID accountId,
                                                             @RequestBody Pageable pageable,
                                                             @RequestPart AccountType type){
        return gatewayService.employeeOperations(token, accountId, pageable, type);
    }

    @GetMapping("/debit-accounts")
    public Page<AccountDto> getAllDebit(@RequestBody String token, @RequestBody Pageable pageable) {
        return gatewayService.getAllDebit(token, pageable);
    }

    @GetMapping("/credit-accounts")
    public Page<AccountDto> getAllCredit(@RequestBody String token, @RequestBody Pageable pageable) {
        return gatewayService.getAllCredit(token, pageable);
    }

}
