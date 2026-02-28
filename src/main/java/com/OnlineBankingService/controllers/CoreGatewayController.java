package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.services.CoreGatewayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

    // -------------------- helpers --------------------

    private String extractToken(String authorization) {
        return authorization.replace("Bearer ", "");
    }

    // -------------------- client actions --------------------

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

    // -------------------- employee actions --------------------

    @GetMapping("/accounts/{accountId}/operations/employee")
    public Page<AccountOperationResponse> employeeOperations(
            @RequestHeader("Authorization") String authorization,
            @PathVariable UUID accountId,
            @RequestParam AccountType type,
            Pageable pageable
    ) {
        return gatewayService.employeeOperations(
                extractToken(authorization),
                accountId,
                pageable,
                type
        );
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


}