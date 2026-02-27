package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.DebitAccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
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
    public DebitAccountDto openAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID clientId
    ) {
        return gatewayService.openAccount(token, clientId);
    }

    @PostMapping("/debit-accounts/{accountId}/close/{clientId}")
    public DebitAccountDto closeAccount(
            @RequestHeader("Authorization") String token,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId
    ) {
        return gatewayService.closeAccount(token, clientId, accountId);
    }

    @PostMapping("/debit-accounts/{accountId}/deposit")
    public DebitAccountDto deposit(@RequestHeader("Authorization") String token,
                                   @PathVariable UUID clientId,
                                   @PathVariable UUID accountId,
                                   @Valid @RequestBody MoneyRequest request){
        return gatewayService.deposit(token, clientId, accountId, request);
    }

    @PostMapping("/debit-accounts/{accountId}/withdraw")
    public DebitAccountDto withdraw(@RequestHeader("Authorization") String token,
                                   @PathVariable UUID clientId,
                                   @PathVariable UUID accountId,
                                   @Valid @RequestBody MoneyRequest request){
        return gatewayService.withdraw(token, clientId, accountId, request);
    }

    @GetMapping("/clients/{clientId}/debit-accounts")
    public List<DebitAccountDto> getByClient(@RequestHeader("Authorization") String token,@PathVariable UUID clientId){
        return gatewayService.getByClient(token, clientId);
    }

    @GetMapping("/debit-accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(@RequestHeader("Authorization") String token,
                                                     @PathVariable UUID clientId,
                                                     @PathVariable UUID accountId,
                                                     Pageable pageable){
        return gatewayService.operations(token, clientId, accountId, pageable);
    }

    @GetMapping("/debit-accounts/{accountId}/operations/employee")
    public Page<AccountOperationResponse> employeeOperations(@RequestHeader("Authorization") String token,
                                                     @PathVariable UUID accountId,
                                                     Pageable pageable){
        return gatewayService.employeeOperations(token, accountId, pageable);
    }

    @GetMapping("/debit-accounts")
    public Page<DebitAccountDto> getAllDebit(@RequestHeader("Authorization") String token, Pageable pageable) {
        return gatewayService.getAllDebit(token, pageable);
    }

}
