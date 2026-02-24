package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.ClientCreditResponse;
import com.OnlineBankingService.entity.dto.CreateClientCreditRequest;
import com.OnlineBankingService.service.ClientCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientCredit")
@RequiredArgsConstructor
public class ClientCreditController {

    private final ClientCreditService service;

    @PostMapping("/create")
    public ClientCreditResponse create(@RequestBody @Valid CreateClientCreditRequest request){
        return service.createClientCredit(request);
    }

    @GetMapping("/getAll")
    public List<ClientCreditResponse> getAll(@RequestParam(required = false) UUID clientId, @RequestParam(required = false) UUID creditTariffId,
                                             @RequestParam(required = false) BigDecimal creditAmountFrom, @RequestParam(required = false) BigDecimal creditAmountTo,
                                             @RequestParam(required = false) BigDecimal debtAmountFrom, @RequestParam(required = false) BigDecimal debtAmountTo,
                                             @RequestParam(required = false) String creditStatus, @RequestParam(required = false) String sortBy,
                                             @RequestParam(defaultValue = "asc") String direction){
        return service.getAllClientCredit(clientId, creditTariffId, creditAmountFrom, creditAmountTo, debtAmountFrom, debtAmountTo, creditStatus, sortBy, direction);
    }

    @GetMapping("/getById/{id}")
    public ClientCreditResponse getById(@PathVariable UUID id){
        return service.getByIdClientCredit(id);
    }

    @PostMapping("/getCurrentClientCredit")
    public List<ClientCreditResponse> getCurrentClientCredit(@RequestBody @Valid AuthValidationRequest request){
        return service.getCurrentClientCredit(request);
    }
}
