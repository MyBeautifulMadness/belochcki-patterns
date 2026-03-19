package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.*;
import com.OnlineBankingService.entities.OperationType;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "credit-service", url = "http://localhost:8084/api")
public interface CreditClient {

    @PostMapping("/clientCredit/create")
    ClientCreditResponse createCredit(@RequestBody @Valid CreateClientCreditRequest request);

    @GetMapping("/clientCredit/getAll")
    Map<String, Object> getAllCredit(@RequestParam(required = false) UUID clientId, @RequestParam(required = false) UUID creditTariffId,
                                      @RequestParam(required = false) BigDecimal creditAmountFrom, @RequestParam(required = false) BigDecimal creditAmountTo,
                                      @RequestParam(required = false) BigDecimal debtAmountFrom, @RequestParam(required = false) BigDecimal debtAmountTo,
                                      @RequestParam(required = false) String creditStatus, @RequestParam(required = false) String sortBy,
                                      @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size);

    @GetMapping("/clientCredit/getById/{id}")
    ClientCreditResponse getById(@PathVariable UUID id);

    @PostMapping("/clientCredit/repay")
    ResponseEntity<String> repay(@RequestBody RepayCreditRequest request);

    @GetMapping("/clientCreditRating")
    Integer getClientCreditRating(@RequestParam UUID clientId);

    @GetMapping("/clientDebts")
    List<ClientCreditDebtResponse> getClientCreditDebts(@RequestParam UUID clientId, @RequestParam(required = false) UUID creditId);

    @GetMapping("/creditOperationHistory/getAll")
    Map<String, Object> getAllOperations(@RequestParam(required = false) UUID clientCreditId, @RequestParam(required = false) OperationType operationType,
                                      @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
                                      @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
                                      @RequestParam(required = false) String sortBy, @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

    @PostMapping("/creditTariff/create")
    CreditTariffResponse createTariff(@RequestBody @Valid CreditTariffRequest creditTariffRequest);

    @DeleteMapping("/creditTariff/delete/{id}")
    void delete(@PathVariable UUID id, @RequestBody DeleteCreditTariffRequest request);

    @GetMapping("/creditTariff/getAll")
    Map<String, Object> getAllTariffs(@RequestParam(required = false) String name, @RequestParam(required = false) String description,
                                      @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
                                      @RequestParam(required = false) BigDecimal interestRate, @RequestParam(required = false) String sortBy,
                                      @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size);

    @GetMapping("/creditTariff/getById/{id}")
    CreditTariffResponse getTariffById(@PathVariable UUID id);

    @PutMapping("/creditTariff/update/{id}")
    CreditTariffResponse update(@PathVariable UUID id, @RequestBody @Valid CreditTariffRequest request);

}
