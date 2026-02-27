package com.OnlineBankingService.entity.dto;

import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.enums.OperationType;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
public class CreditOperationHistoryResponse {

    private UUID id;
    private UUID clientCreditId;
    private LocalDate date;
    private LocalTime time;
    private BigDecimal amount;
    private String comment;
    private OperationType operationType;
}
