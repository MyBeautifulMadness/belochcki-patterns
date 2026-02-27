package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.CreditTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditTariffServiceImpl implements CreditTariffService {

    private final CreditTariffRepository creditTariffRepository;

    @Override
    public CreditTariffResponse createCreditTariff (CreditTariffRequest request) {
        CreditTariff creditTariff = CreditTariff.builder()
                .name(request.getName())
                .description(request.getDescription())
                .amountFrom(request.getAmountFrom())
                .amountTo(request.getAmountTo())
                .interestRate(request.getInterestRate())
                .build();

        CreditTariff result = creditTariffRepository.save(creditTariff);

        return CreditTariffResponse.builder()
                .id(result.getId())
                .name(result.getName())
                .description(result.getDescription())
                .amountFrom(result.getAmountFrom())
                .amountTo(result.getAmountTo())
                .interestRate(result.getInterestRate())
                .build();
    }

    @Override
    public void deleteCreditTariff (UUID id){
        creditTariffRepository.deleteById(id);
    }

    @Override
    public List<CreditTariffResponse> getAllCreditTariff(String name, String description, BigDecimal amountFrom, BigDecimal amountTo, BigDecimal interestRate, String sortBy, String direction){

        Specification<CreditTariff> spec = (root, query, cb) -> {

            var predicate = cb.conjunction();
            if (name != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }

            if (description != null) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
            }

            if (amountFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amountFrom"), amountFrom));
            }

            if (amountTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("amountTo"), amountTo));
            }

            if (interestRate != null) {
                predicate = cb.and(predicate, cb.equal(root.get("interestRate"), interestRate));
            }

            return predicate;
        };

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank()) {

            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(sortDirection, sortBy);
        }

        return creditTariffRepository
                .findAll(spec, sort)
                .stream()
                .map(creditTariff -> CreditTariffResponse.builder()
                        .id(creditTariff.getId())
                        .name(creditTariff.getName())
                        .description(creditTariff.getDescription())
                        .amountFrom(creditTariff.getAmountFrom())
                        .amountTo(creditTariff.getAmountTo())
                        .interestRate(creditTariff.getInterestRate())
                        .build())
                .toList();
    }

    @Override
    public CreditTariffResponse getByIdCreditTariff(UUID id){

        CreditTariff creditTariff = creditTariffRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Данного Кредитного тарифа не существует"));

        return CreditTariffResponse.builder()
                .id(creditTariff.getId())
                .name(creditTariff.getName())
                .description(creditTariff.getDescription())
                .amountFrom(creditTariff.getAmountFrom())
                .amountTo(creditTariff.getAmountTo())
                .interestRate(creditTariff.getInterestRate())
                .build();
    }

    @Override
    public CreditTariffResponse updateCreditTariff(UUID id, CreditTariffRequest request){

        CreditTariff creditTariff = creditTariffRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Данного Кредитного тарифа не существует"));

        creditTariff.setName(request.getName());
        creditTariff.setDescription(request.getDescription());
        creditTariff.setAmountFrom(request.getAmountFrom());
        creditTariff.setAmountTo(request.getAmountTo());
        creditTariff.setInterestRate(request.getInterestRate());

        CreditTariff updetedCreditTariff = creditTariffRepository.save(creditTariff);

        return CreditTariffResponse.builder()
                .id(updetedCreditTariff.getId())
                .name(updetedCreditTariff.getName())
                .description(updetedCreditTariff.getDescription())
                .amountFrom(updetedCreditTariff.getAmountFrom())
                .amountTo(updetedCreditTariff.getAmountTo())
                .interestRate(updetedCreditTariff.getInterestRate())
                .build();
    }
}
