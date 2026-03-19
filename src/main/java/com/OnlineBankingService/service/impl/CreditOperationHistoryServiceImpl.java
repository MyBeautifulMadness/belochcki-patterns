package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.entity.CreditOperationHistory;
import com.OnlineBankingService.entity.dto.CreditOperationHistoryResponse;
import com.OnlineBankingService.entity.enums.OperationType;
import com.OnlineBankingService.repository.CreditOperationHistoryRepository;
import com.OnlineBankingService.service.CreditOperationHistoryService;
import com.OnlineBankingService.service.CreditTariffService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CreditOperationHistoryServiceImpl implements CreditOperationHistoryService {

    private final CreditOperationHistoryRepository creditOperationHistoryRepository;

    @Override
    public Map<String, Object> getAllOperations(UUID clientCreditId, OperationType operationType, LocalDate dateFrom, LocalDate dateTo,
                                                                BigDecimal amountFrom, BigDecimal amountTo, String sortBy, String direction, int page, int size){

        Specification<CreditOperationHistory> spec = (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (clientCreditId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("clientCreditId").get("id"), clientCreditId));
            }

            if (operationType != null) {
                predicate = cb.and(predicate, cb.equal(root.get("operationType"), operationType));
            }

            if (dateFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("date"), dateFrom));
            }

            if (dateTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("date"), dateTo));
            }

            if (amountFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("amount"), amountFrom));
            }

            if (amountTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("amount"), amountTo));
            }

            return predicate;
        };

        Sort sort = Sort.unsorted();

        if (sortBy != null && !sortBy.isBlank()) {
            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;

            sort = Sort.by(sortDirection, sortBy);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CreditOperationHistory> operationPage = creditOperationHistoryRepository.findAll(spec, pageable);

        List<CreditOperationHistoryResponse> data = operationPage.getContent()
                .stream()
                .map(operation -> CreditOperationHistoryResponse.builder()
                        .id(operation.getId())
                        .date(operation.getDate())
                        .time(operation.getTime())
                        .amount(operation.getAmount())
                        .comment(operation.getComment())
                        .operationType(operation.getOperationType())
                        .clientCreditId(operation.getClientCreditId().getId())
                        .clientId(operation.getClientCreditId().getClientId())
                        .build())
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("page", operationPage.getNumber());
        response.put("size", operationPage.getSize());
        response.put("count", operationPage.getTotalPages());
        response.put("totalElements", operationPage.getTotalElements());

        return response;
    }
}
