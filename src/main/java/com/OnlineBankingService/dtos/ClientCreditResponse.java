package com.OnlineBankingService.dtos;

import com.OnlineBankingService.entities.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClientCreditResponse {

    private UUID id;
    private UUID creditTariffId;
    private UUID clientId;
    private LocalDate issueDate;
    private LocalTime issueTime;
    private BigDecimal creditAmount;
    private BigDecimal debtAmount;
    private CreditStatus creditStatus;
    private LocalDate lastPaymentDate;
}
