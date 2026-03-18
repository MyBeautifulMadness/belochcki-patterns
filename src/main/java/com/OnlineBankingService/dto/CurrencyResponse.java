package com.OnlineBankingService.dto;

public record CurrencyResponse(
        String code,
        String name,
        String symbol,
        Boolean isActive
) {
}
