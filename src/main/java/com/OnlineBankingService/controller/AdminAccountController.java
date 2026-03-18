package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.CreditAccountResponse;
import com.OnlineBankingService.dto.DebitAccountResponse;
import com.OnlineBankingService.repository.CreditAccountRepository;
import com.OnlineBankingService.repository.DebitAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/admin")
public class AdminAccountController {

    private final DebitAccountRepository accountRepository;
    private final CreditAccountRepository creditAccountRepository;

    @GetMapping("/debit-accounts")
    public Page<DebitAccountResponse> getAllDebit(Pageable pageable) {
        return accountRepository.findAll(pageable)
                .map(a -> new DebitAccountResponse(
                        a.getId(),
                        a.getClientId(),
                        a.getCreatedDate(),
                        a.getCreatedTime(),
                        a.getBalance(),
                        a.getName(),
                        a.getCurrencyCode(),
                        a.getStatus()
                ));
    }

    @GetMapping("/credit-accounts")
    public Page<CreditAccountResponse> getAllCredit(Pageable pageable) {
        return creditAccountRepository.findAll(pageable)
                .map(a -> new CreditAccountResponse(
                        a.getId(),
                        a.getClientId(),
                        a.getCreatedDate(),
                        a.getCreatedTime(),
                        a.getBalance(),
                        a.getName(),
                        a.getCurrencyCode(),
                        a.getStatus()
                ));
    }
}
