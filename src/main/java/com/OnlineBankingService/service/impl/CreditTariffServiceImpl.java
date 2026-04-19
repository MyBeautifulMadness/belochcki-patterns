package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.config.RestTemplateConfig;
import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.CreditTariffRequest;
import com.OnlineBankingService.entity.dto.CreditTariffResponse;
import com.OnlineBankingService.entity.dto.DeleteCreditTariffRequest;
import com.OnlineBankingService.notifications.NotificationService;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.CreditTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditTariffServiceImpl implements CreditTariffService {

    private final CreditTariffRepository creditTariffRepository;
    private final RestTemplate restTemplateConfig;
    private final NotificationService pushService;

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

        //pushService.sendToUser(request.getClientId(), "{\"title\":\"Новая операция\",\"body\":\"Данные о кредите изменились\"}");
        pushService.sendToAll("{\"title\":\"Операция\",\"body\":\"Создан кредитный тариф\"}");

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
    public void deleteCreditTariff (UUID id, DeleteCreditTariffRequest request){

        pushService.sendToAll("{\"title\":\"Операция\",\"body\":\"Удален кредитный тариф\"}");

        creditTariffRepository.deleteById(id);
    }

    @Override
    public Map<String, Object> getAllCreditTariff(String name, String description, BigDecimal amountFrom, BigDecimal amountTo, BigDecimal interestRate, String sortBy, String direction, int page, int size){

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

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditTariff> tariffPage = creditTariffRepository.findAll(spec, pageable);

        List<CreditTariffResponse> data = tariffPage.getContent()
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

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("page", tariffPage.getNumber());
        response.put("size", tariffPage.getSize());
        response.put("count", tariffPage.getTotalPages());
        response.put("totalElements", tariffPage.getTotalElements());

        return response;
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

        pushService.sendToAll("{\"title\":\"Операция\",\"body\":\"Обновлен кредитный тариф\"}");

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
