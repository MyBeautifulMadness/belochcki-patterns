package com.OnlineBankingService.service;

import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;
import com.OnlineBankingService.entity.dto.DeleteCreditTariffRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface CreditTariffService {

    CreditTariffResponse createCreditTariff(CreditTariffRequest request);

    void deleteCreditTariff(UUID id, DeleteCreditTariffRequest request);

    Map<String, Object> getAllCreditTariff(String name, String description, BigDecimal amountFrom, BigDecimal amountTo, BigDecimal interestRate, String sortBy, String direction, int page, int size);

    CreditTariffResponse getByIdCreditTariff(UUID id);

    CreditTariffResponse updateCreditTariff(UUID id, CreditTariffRequest request);
}
