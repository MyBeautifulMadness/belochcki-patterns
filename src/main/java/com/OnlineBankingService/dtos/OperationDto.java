package com.OnlineBankingService.dtos;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class OperationDto {
    public UUID operationId;
    public LocalDateTime dateTime;
    public BigDecimal amount;
    public String type;
}
