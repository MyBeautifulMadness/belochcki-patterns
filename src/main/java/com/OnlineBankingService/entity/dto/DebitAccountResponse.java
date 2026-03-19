package com.OnlineBankingService.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DebitAccountResponse {

    private UUID id;

    @JsonProperty("CurrencyCode")
    private String CurrencyCode;
}
