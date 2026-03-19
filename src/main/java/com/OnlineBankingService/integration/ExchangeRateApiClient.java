package com.OnlineBankingService.integration;

import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.dto.ExchangeRateApiPairResponse;
import com.OnlineBankingService.service.ExchangeRateClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class ExchangeRateApiClient implements ExchangeRateClient {

    private final RestTemplate restTemplate;

    @Value("${exchange-rate.api.base-url}")
    private String baseUrl;

    @Value("${exchange-rate.api.key}")
    private String apiKey;

    @Override
    public BigDecimal getRate(String fromCurrencyCode, String toCurrencyCode) {
        String from = fromCurrencyCode.toUpperCase();
        String to = toCurrencyCode.toUpperCase();

        if (from.equals(to)) {
            return BigDecimal.ONE;
        }

        String url = String.format("%s/v6/%s/pair/%s/%s", baseUrl, apiKey, from, to);

        ExchangeRateApiPairResponse response =
                restTemplate.getForObject(url, ExchangeRateApiPairResponse.class);

        if (response == null) {
            throw new ConflictException("Exchange rate service returned empty response");
        }

        if (!"success".equalsIgnoreCase(response.result())) {
            throw new ConflictException("Exchange rate service returned error: " + response.result());
        }

        if (response.conversion_rate() == null) {
            throw new NotFoundException("Exchange rate not found for " + from + " -> " + to);
        }

        return response.conversion_rate();
    }
}
