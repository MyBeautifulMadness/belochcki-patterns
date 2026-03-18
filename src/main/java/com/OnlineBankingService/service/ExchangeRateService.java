package com.OnlineBankingService.service;

import java.math.BigDecimal;

public interface ExchangeRateService {

    BigDecimal convert(BigDecimal amount, String fromCurrencyCode, String toCurrencyCode);
}