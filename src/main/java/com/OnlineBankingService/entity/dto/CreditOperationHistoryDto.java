package com.OnlineBankingService.entity.dto;

import com.OnlineBankingService.entity.enums.OperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreditOperationHistoryDto {

    private UUID id;
    private UUID clientCreditId;
    private OperationType operationType;
    private LocalDate date;
}
