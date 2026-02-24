package com.OnlineBankingService.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CreateClientCreditRequest {

    @NotNull
    private UUID creditTariffId;

    @NotNull
    private UUID clientId;

    @NotNull
    private String token;

    @NotNull
    @Positive
    private BigDecimal creditAmount;
}
