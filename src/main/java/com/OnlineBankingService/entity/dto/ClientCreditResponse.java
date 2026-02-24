package com.OnlineBankingService.entity.dto;

import com.OnlineBankingService.entity.enums.CreditStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
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
