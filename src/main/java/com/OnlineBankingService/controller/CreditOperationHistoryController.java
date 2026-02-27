package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.CreditOperationHistoryResponse;
import com.OnlineBankingService.entity.enums.OperationType;
import com.OnlineBankingService.service.CreditOperationHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/creditOperationHistory")
@RequiredArgsConstructor
public class CreditOperationHistoryController {

    private final CreditOperationHistoryService creditOperationHistoryService;

    @GetMapping("/getAll")
    public List<CreditOperationHistoryResponse> getAll(@RequestParam(required = false) UUID clientCreditId, @RequestParam(required = false) OperationType operationType,
            @RequestParam(required = false) LocalDate dateFrom, @RequestParam(required = false) LocalDate dateTo,
            @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
            @RequestParam(required = false) String sortBy, @RequestParam(defaultValue = "asc") String direction) {

        return creditOperationHistoryService.getAllOperations(clientCreditId, operationType, dateFrom, dateTo, amountFrom, amountTo, sortBy, direction);
    }
}
