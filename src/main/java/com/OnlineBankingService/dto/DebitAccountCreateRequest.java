package com.OnlineBankingService.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record DebitAccountCreateRequest(
        @NotNull UUID clientId
) {
}
