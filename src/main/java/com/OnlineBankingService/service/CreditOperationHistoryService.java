package com.OnlineBankingService.service;

import com.OnlineBankingService.entity.dto.CreditOperationHistoryResponse;
import com.OnlineBankingService.entity.enums.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreditOperationHistoryService {

    Map<String, Object> getAllOperations(UUID clientCreditId, OperationType operationType, LocalDate dateFrom, LocalDate dateTo, BigDecimal amountFrom, BigDecimal amountTo, String sortBy, String direction, int page, int size);
}
