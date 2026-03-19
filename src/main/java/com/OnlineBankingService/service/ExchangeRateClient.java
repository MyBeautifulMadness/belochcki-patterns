package com.OnlineBankingService.service;

import java.math.BigDecimal;

public interface ExchangeRateClient {

    BigDecimal getRate(String fromCurrencyCode, String toCurrencyCode);
}