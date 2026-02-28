package com.OnlineBankingService.dtos;

import com.OnlineBankingService.entities.AccountStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

public record AccountDto(
        UUID id,
        UUID clientId,
        LocalDate createdDate,
        LocalTime createdTime,
        BigDecimal balance,
        String name,
        AccountStatus status
) {
}
