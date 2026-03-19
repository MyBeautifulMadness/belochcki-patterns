package com.OnlineBankingService.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCurrencyRequest (
        @NotBlank @Size(min = 3, max = 3)
        String code,

        @NotBlank
        String name,

        String symbol
) {
}
