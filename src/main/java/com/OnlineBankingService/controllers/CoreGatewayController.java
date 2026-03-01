package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.dtos.PagedResponse;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import com.OnlineBankingService.services.CoreGatewayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequestMapping("/api/gateway/accounts")
public class CoreGatewayController {

    private final CoreGatewayService gatewayService;

    public CoreGatewayController(CoreGatewayService gatewayService) {
        this.gatewayService = gatewayService;
    }


    private String extractToken(String authorization) {
        return authorization.replace("Bearer ", "");
    }

    @PostMapping("/clients/{clientId}/debit-accounts")
    public AccountDto openAccount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId
    ) {
        return gatewayService.openAccount(extractToken(authorization), clientId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/close")
    public AccountDto closeAccount(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId
    ) {
        return gatewayService.closeAccount(extractToken(authorization), clientId, accountId);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/deposit")
    public AccountDto deposit(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId,
            @Valid @RequestBody MoneyRequest request
    ) {
        return gatewayService.deposit(extractToken(authorization), clientId, accountId, request);
    }

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/withdraw")
    public AccountDto withdraw(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId,
            @Valid @RequestBody MoneyRequest request
    ) {
        return gatewayService.withdraw(extractToken(authorization), clientId, accountId, request);
    }

    @GetMapping("/clients/{clientId}/debit-accounts")
    public List<AccountDto> getByClient(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId
    ) {
        return gatewayService.getByClient(extractToken(authorization), clientId);
    }

    @GetMapping("/clients/{clientId}/accounts/{accountId}/operations")
    public Page<AccountOperationResponse> operations(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID clientId,
            @PathVariable UUID accountId,
            @RequestParam AccountType accountType,
            Pageable pageable
    ) {
        return gatewayService.operations(
                extractToken(authorization),
                clientId,
                accountId,
                pageable,
                accountType
        );
    }

    @GetMapping("/clients/{clientId}/credit-accounts/{accountId}")
    public AccountDto getByIdCredit(@RequestHeader("Authorization") String authorization, @PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role){
        return gatewayService.getByIdCredit(extractToken(authorization), clientId, accountId, role);
    }

    @GetMapping("/clients/{clientId}/debit-accounts/{accountId}")
    public AccountDto getById(@RequestHeader("Authorization") String authorization, @PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role){
        return gatewayService.getById(extractToken(authorization), clientId, accountId, role);
    }

    @GetMapping("/debit-accounts")
    public Page<AccountDto> getAllDebit(
            @RequestHeader("Authorization") String authorization,
            Pageable pageable
    ) {
        return gatewayService.getAllDebit(extractToken(authorization), pageable);
    }

    @GetMapping("/credit-accounts")
    public Page<AccountDto> getAllCredit(
            @RequestHeader("Authorization") String authorization,
            Pageable pageable
    ) {
        return gatewayService.getAllCredit(extractToken(authorization), pageable);
    }


    @GetMapping("/clients/{clientId}/credit-account")
    public AccountDto getMyCreditAccount(@RequestHeader("Authorization") String authorization, @PathVariable UUID clientId){
        return gatewayService.getMyCreditAccount(extractToken(authorization), clientId);
    }

    @PostMapping("/credit-accounts/{accountId}/withdraw")
    public AccountDto withdrawCredit(@RequestHeader("Authorization") String authorization, @PathVariable UUID clientId,
                                     @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request){
        return gatewayService.withdrawCredit(extractToken(authorization), clientId, accountId, request);
    }

    @GetMapping("/credit-accounts/{accountId}/operations")
    Page<AccountOperationResponse> operationsCredit(@RequestHeader("Authorization") String authorization,
                                                    @PathVariable UUID clientId, @PathVariable UUID accountId,
                                                    Pageable pageable){
        return gatewayService.operationsCredit(extractToken(authorization), clientId, accountId, pageable);
    }


}