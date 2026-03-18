package com.OnlineBankingService.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record CreditIssuedRequest(
        @NotNull UUID clientId,
        @NotNull @DecimalMin(value = "0.01") BigDecimal amount,
        @NotBlank @Size(min = 3, max = 3) String currencyCode,
        String comment
) {
    public String commentOrDefault(String fallback) {
        return (comment == null || comment.isBlank()) ? fallback : comment;
    }
}
