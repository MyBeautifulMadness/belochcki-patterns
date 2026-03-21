package com.OnlineBankingService.kafka.command;

import java.math.BigDecimal;
import java.util.UUID;

public record MasterAccountWithdrawCommand(
        UUID operationId,
        BigDecimal amount,
        String comment
) {
}