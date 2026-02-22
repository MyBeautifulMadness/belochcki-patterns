package com.OnlineBankingService.entity.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreditTariffRequest {

    @NotNull
    private String name;
    private String description;

    @NotNull
    @Positive
    private BigDecimal amountFrom;

    @NotNull
    @Positive
    private BigDecimal amountTo;

    @NotNull
    @Positive
    private BigDecimal interestRate;
}
