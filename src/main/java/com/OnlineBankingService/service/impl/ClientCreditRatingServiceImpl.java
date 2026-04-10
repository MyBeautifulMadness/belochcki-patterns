package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.config.RestTemplateConfig;
import com.OnlineBankingService.entity.CreditOperationHistory;
import com.OnlineBankingService.entity.dto.ClientCreditDebtResponse;
import com.OnlineBankingService.entity.enums.OperationType;
import com.OnlineBankingService.repository.CreditOperationHistoryRepository;
import com.OnlineBankingService.service.ClientCreditRatingService;
import com.OnlineBankingService.service.ClientCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientCreditRatingServiceImpl implements ClientCreditRatingService {

    private final RestTemplate restTemplateConfig;
    private final CreditOperationHistoryRepository creditOperationHistoryRepository;

    @Override
    public Integer getClientCreditRating(UUID clientId) {
        String url = "http://localhost:8082/api/clients/credit-rating?id=" + clientId;

        ResponseEntity<Integer> response = restTemplateConfig.getForEntity(url, Integer.class);

        return response.getBody();
    }
    @Override
    public List<ClientCreditDebtResponse> getClientCreditDebts(UUID clientId, UUID creditId) {

        Sort sort = Sort.by(Sort.Order.desc("date"), Sort.Order.desc("time"));

        List<CreditOperationHistory> operations;

        if (creditId != null) {
            operations = creditOperationHistoryRepository
                    .findByClientCreditId_ClientIdAndClientCreditId_IdAndOperationType(
                            clientId,
                            creditId,
                            OperationType.REPAYMENT,
                            sort
                    );
        } else {
            operations = creditOperationHistoryRepository
                    .findByClientCreditId_ClientIdAndOperationType(
                            clientId,
                            OperationType.REPAYMENT,
                            sort
                    );
        }

        Map<UUID, CreditOperationHistory> latestOperationByCredit = new LinkedHashMap<>();

        for (CreditOperationHistory operation : operations) {
            UUID currentCreditId = operation.getClientCreditId().getId();

            if (!latestOperationByCredit.containsKey(currentCreditId)) {
                latestOperationByCredit.put(currentCreditId, operation);
            }
        }

        LocalDate now = LocalDate.now();

        return latestOperationByCredit.values().stream()
                .filter(operation -> operation.getDate() != null)
                .filter(operation -> ChronoUnit.DAYS.between(operation.getDate(), now) > 30)
                .map(operation -> ClientCreditDebtResponse.builder()
                        .creditId(operation.getClientCreditId().getId())
                        .debtAmount(operation.getClientCreditId().getDebtAmount())
                        .creditAmount(operation.getClientCreditId().getCreditAmount())
                        .lastDepositDate(operation.getDate())
                        .build())
                .toList();
    }

}
