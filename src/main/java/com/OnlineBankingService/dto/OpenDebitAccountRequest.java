package com.OnlineBankingService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OpenDebitAccountRequest(
        @NotBlank @Size(min = 3, max = 3)
        String currencyCode
) {
}
