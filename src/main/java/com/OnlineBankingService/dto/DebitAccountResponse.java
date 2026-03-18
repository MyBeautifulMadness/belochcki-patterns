package com.OnlineBankingService.dto;

import com.OnlineBankingService.domain.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record DebitAccountResponse(
        UUID id,
        UUID clientId,
        LocalDate createdDate,
        LocalTime createdTime,
        BigDecimal balance,
        String name,
        String CurrencyCode,
        AccountStatus status
) {
}

