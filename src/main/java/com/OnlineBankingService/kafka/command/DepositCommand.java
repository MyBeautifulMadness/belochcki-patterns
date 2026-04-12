package com.OnlineBankingService.kafka.command;

import java.math.BigDecimal;
import java.util.UUID;

public record DepositCommand(
        UUID operationId,
        UUID clientId,
        UUID accountId,
        BigDecimal amount,
        String comment,
        String idempotencyKey
) {
}