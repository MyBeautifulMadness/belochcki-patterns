package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.config.RestTemplateConfig;
import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.CreditOperationHistory;
import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.dto.*;
import com.OnlineBankingService.entity.enums.CreditStatus;
import com.OnlineBankingService.entity.enums.OperationType;
import com.OnlineBankingService.repository.ClientCreditRepository;
import com.OnlineBankingService.repository.CreditOperationHistoryRepository;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.ClientCreditService;
import com.OnlineBankingService.config.RetryExecutor;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientCreditServiceImpl implements ClientCreditService {

    private final ClientCreditRepository clientCreditRepository;
    private final CreditTariffRepository creditTariffRepository;
    private final RestTemplate restTemplateConfig;
    private final CreditOperationHistoryRepository creditOperationHistoryRepository;
    private final RetryExecutor retryExecutor;

    @Override
    @Transactional
    public ClientCreditResponse createClientCredit(CreateClientCreditRequest request){
        CreditTariff tariff = creditTariffRepository.findById(request.getCreditTariffId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Данный кредитный тариф не найден"));

        BigDecimal amount = request.getCreditAmount();
        if (amount.compareTo(tariff.getAmountFrom()) < 0 || amount.compareTo(tariff.getAmountTo()) > 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Указанная сумма кредита не входит в диапазон выбранного тарифа");
        }
        ClientCredit credit = ClientCredit.builder()
                .creditTariffId(tariff)
                .clientId(request.getClientId())
                .issueData(LocalDate.now())
                .issueTime(LocalTime.now())
                .creditAmount(amount)
                .debtAmount(amount)
                .creditStatus(CreditStatus.OPEN)
                .lastPaymentDate(null)
                .build();

        ClientCredit result = clientCreditRepository.save(credit);
        Map<String, Object> creditIssueBody = Map.of("clientId", request.getClientId(), "amount", result.getCreditAmount(), "comment", "Создание кредитного счета");

        ResponseEntity<Void> creditIssueResponse = retryExecutor.execute(
                () -> restTemplateConfig.postForEntity(
                        "http://localhost:8081/api/core/credit-issued",
                        creditIssueBody,
                        Void.class
                ),
                "credit -> core credit-issued"
        );

        if (!creditIssueResponse.getStatusCode().is2xxSuccessful()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при создании кредитного счета");
        }

        CreditOperationHistory history = CreditOperationHistory.builder()
                .clientCreditId(result)
                .date(result.getIssueData())
                .time(result.getIssueTime())
                .amount(result.getCreditAmount())
                .comment("Кредит " + result.getId() + " создан на сумму " + result.getCreditAmount())
                .operationType(OperationType.ISSUANCE)
                .build();

        creditOperationHistoryRepository.save(history);

        return ClientCreditResponse.builder()
                .id(result.getId())
                .creditTariffId(result.getCreditTariffId().getId())
                .clientId(result.getClientId())
                .issueDate(result.getIssueData())
                .issueTime(result.getIssueTime())
                .creditAmount(result.getCreditAmount())
                .debtAmount(result.getDebtAmount())
                .creditStatus(result.getCreditStatus())
                .lastPaymentDate(result.getLastPaymentDate())
                .build();
    }


    @Override
    public Map<String, Object> getAllClientCredit(UUID clientId, UUID creditTariffId, BigDecimal creditAmountFrom, BigDecimal creditAmountTo, BigDecimal debtAmountFrom, BigDecimal debtAmountTo, String creditStatus, String sortBy, String direction, int page, int size){
        Specification<ClientCredit> spec = (root, query, cb) -> {

            var predicate = cb.conjunction();

            if (clientId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("clientId"), clientId));
            }

            if (creditTariffId != null) {
                predicate = cb.and(predicate, cb.equal(root.get("creditTariffId").get("id"), creditTariffId));
            }

            if (creditAmountFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("creditAmount"), creditAmountFrom));
            }

            if (creditAmountTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("creditAmount"), creditAmountTo));
            }

            if (debtAmountFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("debtAmount"), debtAmountFrom));
            }

            if (debtAmountTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("debtAmount"), debtAmountTo));
            }

            if (creditStatus != null) {
                predicate = cb.and(predicate, cb.equal(root.get("creditStatus"), creditStatus));
            }

            return predicate;
        };

        Sort sort = Sort.unsorted();
        if (sortBy != null && !sortBy.isBlank()) {

            Sort.Direction sortDirection = "desc".equalsIgnoreCase(direction) ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(sortDirection, sortBy);
        }

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ClientCredit> creditPage = clientCreditRepository.findAll(spec, pageable);

        List<ClientCreditResponse> data = creditPage.getContent()
                .stream()
                .map(credit -> ClientCreditResponse.builder()
                        .id(credit.getId())
                        .creditTariffId(credit.getCreditTariffId().getId())
                        .clientId(credit.getClientId())
                        .issueDate(credit.getIssueData())
                        .issueTime(credit.getIssueTime())
                        .creditAmount(credit.getCreditAmount())
                        .debtAmount(credit.getDebtAmount())
                        .creditStatus(credit.getCreditStatus())
                        .lastPaymentDate(credit.getLastPaymentDate())
                        .build())
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put("data", data);
        response.put("page", creditPage.getNumber());
        response.put("size", creditPage.getSize());
        response.put("count", creditPage.getTotalPages());
        response.put("totalElements", creditPage.getTotalElements());

        return response;
    }


    @Override
    public ClientCreditResponse getByIdClientCredit(UUID id){

        ClientCredit credit = clientCreditRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Данный кредит не найден"));

        return ClientCreditResponse.builder()
                .id(credit.getId())
                .creditTariffId(credit.getCreditTariffId().getId())
                .clientId(credit.getClientId())
                .issueDate(credit.getIssueData())
                .issueTime(credit.getIssueTime())
                .creditAmount(credit.getCreditAmount())
                .debtAmount(credit.getDebtAmount())
                .creditStatus(credit.getCreditStatus())
                .lastPaymentDate(credit.getLastPaymentDate())
                .build();
    }


    @Override
    public List<ClientCreditResponse> getCurrentClientCredit(AuthValidationRequest request){

        return clientCreditRepository.findByClientId(request.getClientId())
                .stream()
                .map(credit -> ClientCreditResponse.builder()
                        .id(credit.getId())
                        .creditTariffId(credit.getCreditTariffId().getId())
                        .clientId(credit.getClientId())
                        .issueDate(credit.getIssueData())
                        .issueTime(credit.getIssueTime())
                        .creditAmount(credit.getCreditAmount())
                        .debtAmount(credit.getDebtAmount())
                        .creditStatus(credit.getCreditStatus())
                        .lastPaymentDate(credit.getLastPaymentDate())
                        .build())
                .toList();
    }


    @Override
    public void repayCredit(RepayCreditRequest request){

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ClientCredit credit = clientCreditRepository.findById(request.getCreditId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,"Данный кредит не найден"));

//        if (!credit.getClientId().equals(request.getClientId())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Кредит не принадлежит данному клиенту");
//        }

        if (credit.getCreditStatus() == CreditStatus.CLOSED) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Кредит уже закрыт");
        }

        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Сумма должна быть больше 0");
        }

        Map<String, Object> withdrawBody = Map.of("amount", request.getAmount(), "comment", "Погашение кредита " + request.getCreditId());

        ResponseEntity<Void> withdrawResponse = retryExecutor.execute(
                () -> restTemplateConfig.postForEntity(
                        "http://localhost:8081/api/core/clients/" + request.getClientId()
                                + "/debit-accounts/" + request.getDebitAccountId() + "/withdraw",
                        withdrawBody,
                        Void.class
                ),
                "credit -> core withdraw for repay"
        );

        if (!withdrawResponse.getStatusCode().is2xxSuccessful()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка списания средств с дебетого счета");
        }

        String currencyCode = getDebitAccountCurrencyCode(request.getClientId(), request.getDebitAccountId());
        BigDecimal amountInRub = convertToRubles(request.getAmount(), currencyCode);

        MoneyRequest masterDepositRequest = new MoneyRequest(amountInRub, "Пополнение master-account при погашении кредита " + request.getCreditId());

        HttpEntity<MoneyRequest> masterDepositEntity = new HttpEntity<>(masterDepositRequest, headers);

        ResponseEntity<Void> masterDepositResponse = retryExecutor.execute(
                () -> restTemplateConfig.postForEntity(
                        "http://localhost:8081/api/core/master-account/internal/deposit",
                        masterDepositEntity,
                        Void.class
                ),
                "credit -> core internal deposit to master account"
        );

        if (!masterDepositResponse.getStatusCode().is2xxSuccessful()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка пополнения master-счета");
        }

        BigDecimal newDebt = credit.getDebtAmount().subtract(amountInRub);
        credit.setLastPaymentDate(LocalDate.now());
        boolean creditJustClosed = false;

        if (newDebt.compareTo(BigDecimal.ZERO) <= 0) {
            credit.setDebtAmount(BigDecimal.ZERO);
            credit.setCreditStatus(CreditStatus.CLOSED);
            creditJustClosed = true;

            CreditOperationHistory closingHistory = CreditOperationHistory.builder()
                    .clientCreditId(credit)
                    .date(LocalDate.now())
                    .time(LocalTime.now())
                    .amount(BigDecimal.ZERO)
                    .comment("Кредит " + credit.getId() + " полностью погашен")
                    .operationType(OperationType.CLOSING)
                    .build();

            creditOperationHistoryRepository.save(closingHistory);

        } else {
            credit.setDebtAmount(newDebt);
        }

        clientCreditRepository.save(credit);

        CreditOperationHistory history = CreditOperationHistory.builder()
                .clientCreditId(credit)
                .date(LocalDate.now())
                .time(LocalTime.now())
                .amount(amountInRub)
                .comment("Кредит " + credit.getId() + " пополнен на сумму " + request.getAmount() + " " + currencyCode + " (" + amountInRub + " RUB)")
                .operationType(OperationType.REPAYMENT)
                .build();

        creditOperationHistoryRepository.save(history);

        if (creditJustClosed) {
            boolean hasOpenCredits = clientCreditRepository.existsByClientIdAndCreditStatus(request.getClientId(), CreditStatus.OPEN);

            if (!hasOpenCredits) {
                ResponseEntity<Void> closeCreditAccountResponse = retryExecutor.execute(
                        () -> restTemplateConfig.postForEntity(
                                "http://localhost:8081/api/core/" + request.getClientId() + "/close",
                                null,
                                Void.class
                        ),
                        "credit -> core close credit account"
                );
            }
        }

    }

    private String getDebitAccountCurrencyCode(UUID clientId, UUID debitAccountId) {
        String url = "http://localhost:8081/api/core/clients/" + clientId + "/debit-accounts/" + debitAccountId + "?role=CLIENT";

        ResponseEntity<DebitAccountResponse> response = retryExecutor.execute(
                () -> restTemplateConfig.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        DebitAccountResponse.class
                ),
                "credit -> core get debit account currency"
        );

        DebitAccountResponse account = response.getBody();

        if (account == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Дебетовый счет не найден");
        }

        return account.getCurrencyCode();
    }

    private BigDecimal convertToRubles(BigDecimal amount, String currencyCode) {
        if (currencyCode == null || currencyCode.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не указан код валюты");
        }

        if ("RUB".equalsIgnoreCase(currencyCode)) {
            return amount;
        }

        try {
            ResponseEntity<String> response = restTemplateConfig.getForEntity(
                    "https://www.cbr-xml-daily.ru/daily_json.js",
                    String.class
            );

            String json = response.getBody();

            if (json == null || json.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось получить курсы валют");
            }

            ObjectMapper objectMapper = new ObjectMapper();
            CbrRatesResponse body = objectMapper.readValue(json, CbrRatesResponse.class);

            if (body.getValute() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не удалось получить курсы валют");
            }

            CbrCurrencyRate rate = body.getValute().get(currencyCode.toUpperCase());

            if (rate == null || rate.getValue() == null || rate.getNominal() == null || rate.getNominal() == 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Не найден курс для валюты " + currencyCode);
            }

            BigDecimal rubPerOneUnit = rate.getValue()
                    .divide(BigDecimal.valueOf(rate.getNominal()), 10, RoundingMode.HALF_UP);

            return amount.multiply(rubPerOneUnit).setScale(2, RoundingMode.HALF_UP);

        } catch (RestClientException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при обращении к сервису курсов валют");
        }
    }

}
