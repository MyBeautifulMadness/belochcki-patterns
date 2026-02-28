package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.ClientCreditResponse;
import com.OnlineBankingService.entity.dto.CreateClientCreditRequest;
import com.OnlineBankingService.entity.dto.RepayCreditRequest;
import com.OnlineBankingService.service.ClientCreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
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
    public Map<String, Object> getAll(@RequestParam(required = false) UUID clientId, @RequestParam(required = false) UUID creditTariffId,
                                            @RequestParam(required = false) BigDecimal creditAmountFrom, @RequestParam(required = false) BigDecimal creditAmountTo,
                                            @RequestParam(required = false) BigDecimal debtAmountFrom, @RequestParam(required = false) BigDecimal debtAmountTo,
                                            @RequestParam(required = false) String creditStatus, @RequestParam(required = false) String sortBy,
                                            @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        return service.getAllClientCredit(clientId, creditTariffId, creditAmountFrom, creditAmountTo, debtAmountFrom, debtAmountTo, creditStatus, sortBy, direction, page, size);
    }

    @GetMapping("/getById/{id}")
    public ClientCreditResponse getById(@PathVariable UUID id){
        return service.getByIdClientCredit(id);
    }

    @PostMapping("/getCurrentClientCredit")
    public List<ClientCreditResponse> getCurrentClientCredit(@RequestBody @Valid AuthValidationRequest request){
        return service.getCurrentClientCredit(request);
    }

    @PostMapping("/repay")
    public ResponseEntity<String> repay(@RequestBody RepayCreditRequest request) {
        service.repayCredit(request);
        return ResponseEntity.ok("Кредит успешно погашен");
    }
}
