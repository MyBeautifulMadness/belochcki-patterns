package com.OnlineBankingService.dtos;

public record CurrencyResponse(
        String code,
        String name,
        String symbol,
        Boolean isActive
) {
}
