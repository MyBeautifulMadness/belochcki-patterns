package com.OnlineBankingService.controller;

import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.CreditTariffService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public List<CreditTariffResponse> getAll(){
        return service.getAllCreditTariff();
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
