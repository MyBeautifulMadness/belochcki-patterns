package com.OnlineBankingService.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record TransferResponse(
        UUID fromAccountId,
        UUID toAccountId,
        BigDecimal debitedAmount,
        BigDecimal creditedAmount,
        String fromCurrencyCode,
        String toCurrencyCode
) {
}
