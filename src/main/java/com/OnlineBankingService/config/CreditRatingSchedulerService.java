package com.OnlineBankingService.config;

import com.OnlineBankingService.entity.dto.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreditRatingSchedulerService {

    private final RestTemplate restTemplateConfig;

    private static final String CREDIT_OPERATION_HISTORY_URL = "http://localhost:8084/api/creditOperationHistory/getAll?operationType=REPAYMENT&direction=asc&page=0&size=1000";

    private static final String CLIENT_CREDIT_URL = "http://localhost:8084/api/clientCredit/getAll?direction=asc&page=0&size=1000";

    private static final String UPDATE_CREDIT_RATING_URL = "http://localhost:8082/api/clients/credit-rating";

    @Scheduled(fixedRate = 60000) //60000
    @Transactional
    public void updateClientCreditRatings(){

        log.info("Началось обновление кредитных рейтингов");

        ResponseEntity<CreditOperationHistoryPageResponse> historyResponse = restTemplateConfig.getForEntity(CREDIT_OPERATION_HISTORY_URL, CreditOperationHistoryPageResponse.class);

        List<CreditOperationHistoryDto> histories = historyResponse.getBody() != null ? historyResponse.getBody().getData() : Collections.emptyList();

        if (histories == null || histories.isEmpty()) {
            log.info("Нет иторий операций со статусом ПОПОЛНЕНИЕ");
            return;
        }

        ResponseEntity<ClientCreditPageResponse> clientCreditResponse = restTemplateConfig.getForEntity(CLIENT_CREDIT_URL, ClientCreditPageResponse.class);

        List<ClientCreditDto> clientCredits = clientCreditResponse.getBody() != null ? clientCreditResponse.getBody().getData() : Collections.emptyList();

        if (clientCredits == null || clientCredits.isEmpty()) {
            log.warn("Клиентские кредиты не найдены");
            return;
        }

        Map<UUID, ClientCreditDto> clientCreditMap = clientCredits.stream().collect(Collectors.toMap(ClientCreditDto::getId, Function.identity()));

        for (CreditOperationHistoryDto history : histories) {
            try {
                UUID clientCreditId = history.getClientCreditId();
                LocalDate operationDate = history.getDate();
                LocalTime operationTime = history.getTime();

                if (clientCreditId == null || operationDate == null || operationTime == null) {
                    log.warn("История historyId={} пропущена, потому что значение clientCreditId или даты/времени null", history.getId());
                    continue;
                }

                ClientCreditDto clientCreditDto = clientCreditMap.get(clientCreditId);

                if (clientCreditDto == null) {
                    log.warn("Клиентский кредит не найден для clientCreditId={}", clientCreditId);
                    continue;
                }

                UUID clientId = clientCreditDto.getClientId();

                if (clientId == null) {
                    log.warn("id клиента равен null для clientCreditId={}", clientCreditId);
                    continue;
                }

                LocalDateTime operationDateTime = LocalDateTime.of(operationDate, operationTime);

                long minutesBetween = ChronoUnit.MINUTES.between(operationDateTime, LocalDateTime.now());
                int ratingValue = minutesBetween >= 1 ? -10 : 10;

                UpdateCreditRatingRequest request = UpdateCreditRatingRequest.builder()
                        .userId(clientId)
                        .creditRating(ratingValue)
                        .build();

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<UpdateCreditRatingRequest> entity = new HttpEntity<>(request, headers);

                restTemplateConfig.put(UPDATE_CREDIT_RATING_URL, entity);

                log.info("Кредитный рейтинг обновлен для ClientID={}, рейтинг={}", clientId, ratingValue);

            } catch (Exception e) {
                log.error("Ошибка при обработке historyId={}", history.getId(), e);
            }
        }

        log.info("Завершено обновление кредитных рейтингов клиентов");

    }
}
