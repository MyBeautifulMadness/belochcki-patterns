package com.OnlineBankingService.dtos;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
public class CreditTariffResponse {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal interestRate;
}
