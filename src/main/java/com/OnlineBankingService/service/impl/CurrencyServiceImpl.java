package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.dto.CreateCurrencyRequest;
import com.OnlineBankingService.dto.CurrencyResponse;
import com.OnlineBankingService.entity.Currency;
import com.OnlineBankingService.exception.ConflictException;
import com.OnlineBankingService.exception.NotFoundException;
import com.OnlineBankingService.repository.CurrencyRepository;
import com.OnlineBankingService.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final CurrencyRepository currencyRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CurrencyResponse> getAll() {
        return currencyRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CurrencyResponse create(CreateCurrencyRequest request) {
        String code = request.code().toUpperCase();

        if (currencyRepository.existsByCode(code)) {
            throw new ConflictException("Currency already exists: " + code);
        }

        Currency currency = Currency.builder()
                .code(code)
                .name(request.name())
                .symbol(request.symbol())
                .isActive(true)
                .build();

        currency = currencyRepository.save(currency);
        return toResponse(currency);
    }

    @Override
    @Transactional
    public CurrencyResponse deactivate(String code) {
        Currency currency = currencyRepository.findById(code.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Currency not found: " + code));

        currency.setIsActive(false);
        currency = currencyRepository.save(currency);

        return toResponse(currency);
    }

    @Override
    @Transactional(readOnly = true)
    public Currency getActiveCurrencyOrThrow(String code) {
        Currency currency = currencyRepository.findById(code.toUpperCase())
                .orElseThrow(() -> new NotFoundException("Currency not found: " + code));

        if (!Boolean.TRUE.equals(currency.getIsActive())) {
            throw new ConflictException("Currency is inactive: " + code);
        }

        return currency;
    }

    private CurrencyResponse toResponse(Currency currency) {
        return new CurrencyResponse(
                currency.getCode(),
                currency.getName(),
                currency.getSymbol(),
                currency.getIsActive()
        );
    }
}
