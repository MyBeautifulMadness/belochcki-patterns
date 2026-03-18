package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.service.ExchangeRateService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private static final Map<String, BigDecimal> TO_RUB = Map.of(
            "RUB", BigDecimal.ONE,
            "USD", new BigDecimal("90.00"),
            "EUR", new BigDecimal("98.00")
    );

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode) {
        if (fromCurrencyCode.equals(toCurrencyCode)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal fromRate = TO_RUB.get(fromCurrencyCode);
        BigDecimal toRate = TO_RUB.get(toCurrencyCode);

        if (fromRate == null) {
            throw new NotFoundException("Exchange rate not found for currency: " + fromCurrencyCode);
        }
        if (toRate == null) {
            throw new NotFoundException("Exchange rate not found for currency: " + toCurrencyCode);
        }

        BigDecimal amountInRub = amount.multiply(fromRate);
        return amountInRub.divide(toRate, 2, RoundingMode.HALF_UP);
    }
}