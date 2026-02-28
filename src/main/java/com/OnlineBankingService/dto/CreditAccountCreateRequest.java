package com.OnlineBankingService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditAccountCreateRequest(
        @NotNull UUID clientId,
        @NotBlank String name,
        @NotBlank BigDecimal amount
        ) {
}
