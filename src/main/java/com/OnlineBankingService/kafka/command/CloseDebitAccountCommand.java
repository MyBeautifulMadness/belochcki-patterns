package com.OnlineBankingService.kafka.command;

import java.util.UUID;

public record CloseDebitAccountCommand(
        UUID operationId,
        UUID clientId,
        UUID accountId
) {
}