package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.CreditClient;
import com.OnlineBankingService.configs.RetryExecutor;
import com.OnlineBankingService.dtos.*;
import com.OnlineBankingService.entities.OperationType;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class CreditController {

    private final CreditClient creditClient;
    private final RetryExecutor retryExecutor;

    public CreditController(CreditClient creditClient, RetryExecutor retryExecutor) {
        this.creditClient = creditClient;
        this.retryExecutor = retryExecutor;
    }

    @PostMapping("/clientCredit/create")
    ClientCreditResponse create(@RequestBody @Valid CreateClientCreditRequest request) {
        return retryExecutor.execute(() -> creditClient.createCredit(request), "gateway -> credit create credit");
    }

    @GetMapping("/clientCredit/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) UUID clientId,
                               @RequestParam(required = false) UUID creditTariffId,
                               @RequestParam(required = false) BigDecimal creditAmountFrom,
                               @RequestParam(required = false) BigDecimal creditAmountTo,
                               @RequestParam(required = false) BigDecimal debtAmountFrom,
                               @RequestParam(required = false) BigDecimal debtAmountTo,
                               @RequestParam(required = false) String creditStatus,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "asc") String direction,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return retryExecutor.execute(
                () -> creditClient.getAllCredit(clientId, creditTariffId, creditAmountFrom, creditAmountTo,
                        debtAmountFrom, debtAmountTo, creditStatus, sortBy, direction, page, size),
                "gateway -> credit get all credits"
        );
    }

    @GetMapping("/clientCredit/getById/{id}")
    ClientCreditResponse getById(@PathVariable UUID id) {
        return retryExecutor.execute(() -> creditClient.getById(id), "gateway -> credit get credit by id");
    }

    @PostMapping("/clientCredit/repay")
    ResponseEntity<String> repay(@RequestBody RepayCreditRequest request) {
        return retryExecutor.execute(() -> creditClient.repay(request), "gateway -> credit repay");
    }

    @GetMapping("/clientCreditRating")
    Integer getClientCreditRating(@RequestParam UUID clientId) {
        return retryExecutor.execute(() -> creditClient.getClientCreditRating(clientId), "gateway -> credit get client credit rating");
    }

    @GetMapping("/clientDebts")
    List<ClientCreditDebtResponse> getClientCreditDebts(@RequestParam UUID clientId, @RequestParam(required = false) UUID creditId) {
        return retryExecutor.execute(() -> creditClient.getClientCreditDebts(clientId, creditId), "gateway -> credit get client debts");
    }

    @GetMapping("/creditOperationHistory/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) UUID clientCreditId,
                               @RequestParam(required = false) OperationType operationType,
                               @RequestParam(required = false) LocalDate dateFrom,
                               @RequestParam(required = false) LocalDate dateTo,
                               @RequestParam(required = false) BigDecimal amountFrom,
                               @RequestParam(required = false) BigDecimal amountTo,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "asc") String direction,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return retryExecutor.execute(
                () -> creditClient.getAllOperations(clientCreditId, operationType, dateFrom, dateTo, amountFrom, amountTo, sortBy, direction, page, size),
                "gateway -> credit get operation history"
        );
    }

    @PostMapping("/creditTariff/create")
    CreditTariffResponse create(@RequestBody @Valid CreditTariffRequest creditTariffRequest) {
        return retryExecutor.execute(() -> creditClient.createTariff(creditTariffRequest), "gateway -> credit create tariff");
    }

    @DeleteMapping("/creditTariff/delete/{id}")
    void delete(@PathVariable UUID id, @RequestBody DeleteCreditTariffRequest request) {
        retryExecutor.executeVoid(() -> creditClient.delete(id, request), "gateway -> credit delete tariff");
    }

    @GetMapping("/creditTariff/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) String name,
                               @RequestParam(required = false) String description,
                               @RequestParam(required = false) BigDecimal amountFrom,
                               @RequestParam(required = false) BigDecimal amountTo,
                               @RequestParam(required = false) BigDecimal interestRate,
                               @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "asc") String direction,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        return retryExecutor.execute(
                () -> creditClient.getAllTariffs(name, description, amountFrom, amountTo, interestRate, sortBy, direction, page, size),
                "gateway -> credit get all tariffs"
        );
    }

    @GetMapping("/creditTariff/getById/{id}")
    CreditTariffResponse getTariffById(@PathVariable UUID id) {
        return retryExecutor.execute(() -> creditClient.getTariffById(id), "gateway -> credit get tariff by id");
    }

    @PutMapping("/creditTariff/update/{id}")
    CreditTariffResponse update(@PathVariable UUID id, @RequestBody @Valid CreditTariffRequest request) {
        return retryExecutor.execute(() -> creditClient.update(id, request), "gateway -> credit update tariff");
    }
}
