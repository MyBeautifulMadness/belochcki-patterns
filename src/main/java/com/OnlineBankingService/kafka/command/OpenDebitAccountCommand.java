package com.OnlineBankingService.kafka.command;

import java.util.UUID;

public record OpenDebitAccountCommand(
        UUID operationId,
        UUID clientId,
        String currencyCode
) {
}