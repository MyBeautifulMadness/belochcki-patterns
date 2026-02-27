package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(url = "http://localhost:8081/api/core")
public interface CoreClient {

    @PostMapping("/debit-accounts")
    AccountDto openAccount(@PathVariable UUID clientId);

    @PostMapping("/debit-accounts/{accountId}/close")
    AccountDto closeAccount(@PathVariable UUID accountId, @RequestBody UUID clientId);

    @PostMapping("/debit-accounts/{accountId}/deposit")
    AccountDto deposit(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, @RequestBody UUID clientId);

    @PostMapping("/accounts/{accountId}/withdraw")
    AccountDto withdraw(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, @RequestBody UUID clientId, AccountType accountType);

    @GetMapping("/clients/{clientId}/accounts")
    List<AccountDto> getByClient(@PathVariable UUID clientId, AccountType accountType);

    @GetMapping("/accounts/{accountId}/operations")
    Page<AccountOperationResponse> operations(@PathVariable UUID accountId, Pageable pageable, UUID clientId, Role role, AccountType accountType);

    @GetMapping("/accounts")
    Page<AccountDto> getAll(Pageable pageable);
}
