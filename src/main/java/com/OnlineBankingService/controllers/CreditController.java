package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.ClientClient;
import com.OnlineBankingService.configs.CreditClient;
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
@RequestMapping("http://localhost:8084/api")
public class CreditController {


    private final CreditClient creditClient;

    public CreditController(CreditClient creditClient) {
        this.creditClient = creditClient;
    }

    @PostMapping("/clientCredit/create")
    ClientCreditResponse create(@RequestBody @Valid CreateClientCreditRequest request){
        return creditClient.createCredit(request);
    }

    @GetMapping("/clientCredit/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) UUID clientId, @RequestParam(required = false) UUID creditTariffId,
                               @RequestParam(required = false) BigDecimal creditAmountFrom, @RequestParam(required = false) BigDecimal creditAmountTo,
                               @RequestParam(required = false) BigDecimal debtAmountFrom, @RequestParam(required = false) BigDecimal debtAmountTo,
                               @RequestParam(required = false) String creditStatus, @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size){
        return creditClient.getAllCredit(clientId, creditTariffId, creditAmountFrom, creditAmountTo, debtAmountFrom, debtAmountTo, creditStatus, sortBy, direction, page, size);
    }

    @GetMapping("/clientCredit/getById/{id}")
    ClientCreditResponse getById(@PathVariable UUID id){
        return creditClient.getById(id);
    }

    @PostMapping("/clientCredit/repay")
    ResponseEntity<String> repay(@RequestBody RepayCreditRequest request){
        return creditClient.repay(request);
    }

    @GetMapping("/clientCreditRating")
    Integer getClientCreditRating(@RequestParam UUID clientId){
        return creditClient.getClientCreditRating(clientId);
    }

    @GetMapping("/clientDebts")
    List<ClientCreditDebtResponse> getClientCreditDebts(@RequestParam UUID clientId, @RequestParam(required = false) UUID creditId){
        return creditClient.getClientCreditDebts(clientId, creditId);
    }

    @GetMapping("/creditOperationHistory/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) UUID clientCreditId, @RequestParam(required = false) OperationType operationType,
                               @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
                               @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
                               @RequestParam(required = false) String sortBy, @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return creditClient.getAllOperations(clientCreditId, operationType, dateFrom, dateTo, amountFrom, amountTo, sortBy, direction, page, size);
    }

    @PostMapping("/creditTariff/create")
    CreditTariffResponse create(@RequestBody @Valid CreditTariffRequest creditTariffRequest){
        return creditClient.createTariff(creditTariffRequest);
    }

    @DeleteMapping("/creditTariff/delete/{id}")
    void delete(@PathVariable UUID id, @RequestBody DeleteCreditTariffRequest request){
        creditClient.delete(id,request);
    }

    @GetMapping("/creditTariff/getAll")
    Map<String, Object> getAll(@RequestParam(required = false) String name, @RequestParam(required = false) String description,
                               @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
                               @RequestParam(required = false) BigDecimal interestRate, @RequestParam(required = false) String sortBy,
                               @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return creditClient.getAllTariffs(name, description, amountFrom, amountTo, interestRate, sortBy, direction, page, size);
    }

    @GetMapping("/creditTariff/getById/{id}")
    CreditTariffResponse getTariffById(@PathVariable UUID id){
        return creditClient.getTariffById(id);
    }

    @PutMapping("/creditTariff/update/{id}")
    CreditTariffResponse update(@PathVariable UUID id, @RequestBody @Valid CreditTariffRequest request){
        return creditClient.update(id,request);
    }
}
