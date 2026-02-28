package com.OnlineBankingService.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditIssuedRequest(
        @NotNull UUID clientId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        String comment
) {
    public String commentOrDefault(String fallback) {
        return (comment == null || comment.isBlank()) ? fallback : comment;
    }
}
