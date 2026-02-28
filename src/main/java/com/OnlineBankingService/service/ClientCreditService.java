package com.OnlineBankingService.service;

import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.ClientCreditResponse;
import com.OnlineBankingService.entity.dto.CreateClientCreditRequest;
import com.OnlineBankingService.entity.dto.RepayCreditRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public interface ClientCreditService {

    ClientCreditResponse createClientCredit(CreateClientCreditRequest request);

    Map<String, Object> getAllClientCredit(UUID clientId, UUID creditTariffId, BigDecimal creditAmountFrom, BigDecimal creditAmountTo, BigDecimal debtAmountFrom, BigDecimal debtAmountTo, String creditStatus, String sortBy, String direction, int page, int size);

    ClientCreditResponse getByIdClientCredit(UUID id);

    List<ClientCreditResponse> getCurrentClientCredit(AuthValidationRequest request);

    void repayCredit(RepayCreditRequest request);
}
