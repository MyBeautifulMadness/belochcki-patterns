package com.OnlineBankingService.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RepayCreditRequest {
    private UUID clientId;
    private String token;
    private UUID creditId;
    private UUID debitAccountId;
    private BigDecimal amount;
}