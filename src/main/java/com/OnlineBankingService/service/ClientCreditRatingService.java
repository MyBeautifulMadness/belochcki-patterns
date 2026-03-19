package com.OnlineBankingService.service;


import com.OnlineBankingService.entity.dto.ClientCreditDebtResponse;

import java.util.List;
import java.util.UUID;

public interface ClientCreditRatingService {
    Integer getClientCreditRating(UUID clientId);
    List<ClientCreditDebtResponse> getClientCreditDebts(UUID clientId, UUID creditId);
}
