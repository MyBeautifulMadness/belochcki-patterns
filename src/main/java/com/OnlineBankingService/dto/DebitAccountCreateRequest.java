package com.OnlineBankingService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DebitAccountCreateRequest(
        @NotNull Long clientId,
        @NotBlank String name
) {
}
