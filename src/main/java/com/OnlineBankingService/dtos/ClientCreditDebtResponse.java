package com.OnlineBankingService.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClientCreditDebtResponse {

    private UUID creditId;
    private BigDecimal debtAmount;
    private BigDecimal creditAmount;
    private LocalDate lastDepositDate;
}
