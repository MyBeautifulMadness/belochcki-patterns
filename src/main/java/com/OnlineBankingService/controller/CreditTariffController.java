package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.CreditTariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/creditTariff")
@RequiredArgsConstructor
public class CreditTariffController {

    private final CreditTariffService service;

    @PostMapping("/create")
    public CreditTariffResponse create(@RequestBody @Valid CreditTariffRequest creditTariffRequest){
        return service.createCreditTariff(creditTariffRequest);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable UUID id){
        service.deleteCreditTariff(id);
    }

    @GetMapping("/getAll")
    public Map<String, Object> getAll(@RequestParam(required = false) String name, @RequestParam(required = false) String description,
                                            @RequestParam(required = false) BigDecimal amountFrom, @RequestParam(required = false) BigDecimal amountTo,
                                            @RequestParam(required = false) BigDecimal interestRate, @RequestParam(required = false) String sortBy,
                                            @RequestParam(defaultValue = "asc") String direction, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size){
        return service.getAllCreditTariff(name, description, amountFrom, amountTo, interestRate, sortBy, direction, page, size);
    }

    @GetMapping("/getById/{id}")
    public CreditTariffResponse getById(@PathVariable UUID id){
        return service.getByIdCreditTariff(id);
    }

    @PutMapping("/update/{id}")
    public CreditTariffResponse update(@PathVariable UUID id, @RequestBody @Valid CreditTariffRequest request){
        return service.updateCreditTariff(id, request);
    }
}
