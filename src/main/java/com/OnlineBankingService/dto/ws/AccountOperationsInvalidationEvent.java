package com.OnlineBankingService.dto.ws;

import java.util.UUID;

public record AccountOperationsInvalidationEvent(
        String type,
        UUID accountId
) {
    public static AccountOperationsInvalidationEvent forAccount(UUID accountId) {
        return new AccountOperationsInvalidationEvent(
                "ACCOUNT_OPERATIONS_INVALIDATED",
                accountId
        );
    }
}