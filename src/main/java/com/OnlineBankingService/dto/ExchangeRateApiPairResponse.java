package com.OnlineBankingService.dto;

import java.math.BigDecimal;

public record ExchangeRateApiPairResponse(
        String result,
        String documentation,
        String terms_of_use,
        Long time_last_update_unix,
        String time_last_update_utc,
        Long time_next_update_unix,
        String time_next_update_utc,
        String base_code,
        String target_code,
        BigDecimal conversion_rate
) {
}
