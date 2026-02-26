package com.OnlineBankingService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreditAccountCreateRequest(
        @NotNull Long clientId,
        @NotBlank String name
) {
}
