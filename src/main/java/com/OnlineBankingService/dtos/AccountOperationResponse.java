package com.OnlineBankingService.dtos;

import com.OnlineBankingService.entities.OperationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AccountOperationResponse(
        UUID id,
        UUID accountId,
        LocalDate date,
        LocalTime time,
        BigDecimal amount,
        String comment,
        OperationType operationType
) {
}
