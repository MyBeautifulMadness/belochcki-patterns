package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.service.ExchangeRateClient;
import com.OnlineBankingService.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class ExchangeRateServiceImpl implements ExchangeRateService {

    private final ExchangeRateClient exchangeRateClient;

    @Override
    public BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode) {
        if (fromCurrencyCode.equalsIgnoreCase(toCurrencyCode)) {
            return amount.setScale(2, RoundingMode.HALF_UP);
        }

        BigDecimal rate = exchangeRateClient.getRate(fromCurrencyCode, toCurrencyCode);
        return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal getRate(String fromCurrencyCode, String toCurrencyCode) {
        return exchangeRateClient.getRate(fromCurrencyCode, toCurrencyCode);
    }
}