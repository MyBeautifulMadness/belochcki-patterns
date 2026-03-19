package com.OnlineBankingService.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CbrCurrencyRate {

    @JsonProperty("Nominal")
    private Integer nominal;

    @JsonProperty("Value")
    private BigDecimal value;
}
