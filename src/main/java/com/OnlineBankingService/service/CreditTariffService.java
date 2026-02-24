package com.OnlineBankingService.service;

import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CreditTariffService {

    CreditTariffResponse createCreditTariff(CreditTariffRequest request);

    void deleteCreditTariff(UUID id);

    List<CreditTariffResponse> getAllCreditTariff(String name, String description, BigDecimal amountFrom, BigDecimal amountTo, BigDecimal interestRate, String sortBy, String direction);

    CreditTariffResponse getByIdCreditTariff(UUID id);

    CreditTariffResponse updateCreditTariff(UUID id, CreditTariffRequest request);
}
