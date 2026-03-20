package com.OnlineBankingService.kafka.command;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferCommand(
        UUID operationId,
        UUID clientId,
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal amount,
        String comment
) {
}
