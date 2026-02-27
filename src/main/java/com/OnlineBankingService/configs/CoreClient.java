package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.AccountOperationResponse;
import com.OnlineBankingService.dtos.DebitAccountDto;
import com.OnlineBankingService.dtos.MoneyRequest;
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
    DebitAccountDto openAccount(@PathVariable UUID clientId);

    @PostMapping("/debit-accounts/{accountId}/close")
    DebitAccountDto closeAccount(@PathVariable UUID accountId, @RequestBody UUID clientId);

    @PostMapping("/debit-accounts/{accountId}/deposit")
    DebitAccountDto deposit(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, @RequestBody UUID clientId);

    @PostMapping("/debit-accounts/{accountId}/withdraw")
    DebitAccountDto withdraw(@PathVariable UUID accountId, @Valid @RequestBody MoneyRequest request, @RequestBody UUID clientId);

    @GetMapping("/clients/{clientId}/debit-accounts")
    List<DebitAccountDto> getByClient(@PathVariable UUID clientId);

    @GetMapping("/debit-accounts/{accountId}/operations")
    Page<AccountOperationResponse> operations(@PathVariable UUID accountId, Pageable pageable, UUID clientId, Role role);

    @GetMapping("/debit-accounts")
    Page<DebitAccountDto> getAllDebit(Pageable pageable);
}
