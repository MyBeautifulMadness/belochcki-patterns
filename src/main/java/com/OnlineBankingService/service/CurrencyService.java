package com.OnlineBankingService.service;

import com.OnlineBankingService.dto.CreateCurrencyRequest;
import com.OnlineBankingService.dto.CurrencyResponse;
import com.OnlineBankingService.entity.Currency;

import java.util.List;

public interface CurrencyService {

    List<CurrencyResponse> getAll();

    CurrencyResponse create(CreateCurrencyRequest request);

    CurrencyResponse deactivate(String code);

    Currency getActiveCurrencyOrThrow(String code);
}
