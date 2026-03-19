package com.OnlineBankingService.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
public class CbrRatesResponse {

    @JsonProperty("Valute")
    private Map<String, CbrCurrencyRate> valute;
}
