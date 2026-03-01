package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.AccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
import com.OnlineBankingService.dtos.PagedResponse;
import com.OnlineBankingService.entities.AccountType;
import com.OnlineBankingService.entities.Role;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "core-service", url = "http://localhost:8081/api/core")
public interface CoreClient {

    @PostMapping("/clients/{clientId}/debit-accounts")
    AccountDto openAccount(@PathVariable UUID clientId);

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/close")
    AccountDto closeAccount(@PathVariable UUID clientId, @PathVariable UUID accountId);

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/deposit")
    AccountDto deposit(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request);

    @PostMapping("/clients/{clientId}/debit-accounts/{accountId}/withdraw")
    AccountDto withdraw(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request);

    @GetMapping("/clients/{clientId}/debit-accounts")
    List<AccountDto> getByClient(@PathVariable UUID clientId);

    @GetMapping("/clients/{clientId}/accounts/{accountId}/operations")
    PagedResponse<AccountOperationResponse> operations(@PathVariable UUID clientId, @PathVariable UUID accountId,
                                              Pageable pageable,
                                              @RequestParam Role role, @RequestParam AccountType accountType);

    @GetMapping("/admin/debit-accounts")
    PagedResponse<AccountDto> getAllDebit(Pageable pageable);

    @GetMapping("/admin/credit-accounts")
    PagedResponse<AccountDto> getAllCredit(Pageable pageable);

    @GetMapping("/clients/{clientId}/credit-accounts/{accountId}")
    AccountDto getByIdCredit(@PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role);

    @GetMapping("/clients/{clientId}/debit-accounts/{accountId}")
    AccountDto getById(@PathVariable UUID clientId, @PathVariable UUID accountId, @RequestParam Role role);

    @GetMapping("/clients/{clientId}/credit-account")
    AccountDto getMyCreditAccount(@PathVariable UUID clientId);

    @PostMapping("/credit-accounts/{accountId}/withdraw")
    AccountDto withdrawCredit(@PathVariable UUID clientId, @PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request);

    @GetMapping("/credit-accounts/{accountId}/operations")
   PagedResponse<AccountOperationResponse> operationsCredit(@PathVariable UUID clientId, @PathVariable UUID accountId, Pageable pageable);
}
