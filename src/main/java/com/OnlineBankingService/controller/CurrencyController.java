package com.OnlineBankingService.controller;

import com.OnlineBankingService.dto.CreateCurrencyRequest;
import com.OnlineBankingService.dto.CurrencyResponse;
import com.OnlineBankingService.service.CurrencyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/core/currencies")
public class CurrencyController {

    private final CurrencyService currencyService;

    @GetMapping
    public List<CurrencyResponse> getAll() {
        return currencyService.getAll();
    }

    @PostMapping
    public CurrencyResponse create(@Valid @RequestBody CreateCurrencyRequest request) {
        return currencyService.create(request);
    }

    @PatchMapping("/{code}/deactivate")
    public CurrencyResponse deactivate(@PathVariable String code) {
        return currencyService.deactivate(code);
    }
}