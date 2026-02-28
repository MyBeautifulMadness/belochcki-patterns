package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.config.RestTemplateConfig;
import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.CreditOperationHistory;
import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.ClientCreditResponse;
import com.OnlineBankingService.entity.dto.CreateClientCreditRequest;
import com.OnlineBankingService.entity.dto.RepayCreditRequest;
import com.OnlineBankingService.entity.enums.CreditStatus;
import com.OnlineBankingService.entity.enums.OperationType;
import com.OnlineBankingService.repository.ClientCreditRepository;
import com.OnlineBankingService.repository.CreditOperationHistoryRepository;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.ClientCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientCreditServiceImpl implements ClientCreditService {

    private final ClientCreditRepository clientCreditRepository;
    private final CreditTariffRepository creditTariffRepository;
    private final RestTemplateConfig restTemplateConfig;
    private final CreditOperationHistoryRepository creditOperationHistoryRepository;

    @Override
    public ClientCreditResponse createClientCredit(CreateClientCreditRequest request){

        AuthValidationRequest authValidationRequest = AuthValidationRequest.builder()
                .token(request.getToken())
                .clientId(request.getClientId())
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthValidationRequest> entity = new HttpEntity<>(authValidationRequest, headers);

        ResponseEntity<Boolean> response = restTemplateConfig.restTemplate().postForEntity("http://localhost:8085/api/auth/validate-client", entity, Boolean.class);

        if (!response.getStatusCode().is2xxSuccessful() || Boolean.FALSE.equals(response.getBody())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при проверке клиента");
        }

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
        ResponseEntity<Void> creditIssueResponse = restTemplateConfig.restTemplate().postForEntity("http://localhost:8081/api/core/credit-issued", creditIssueBody, Void.class);

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

        AuthValidationRequest authRequest = AuthValidationRequest.builder().token(request.getToken()).clientId(request.getClientId()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthValidationRequest> entity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<Boolean> response = restTemplateConfig.restTemplate().postForEntity("http://localhost:8085/api/auth/validate-client", entity, Boolean.class);

        if (!response.getStatusCode().is2xxSuccessful() || Boolean.FALSE.equals(response.getBody())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при проверке клиента");
        }

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

        AuthValidationRequest authValidationRequest = AuthValidationRequest.builder().token(request.getToken()).clientId(request.getClientId()).build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthValidationRequest> entity = new HttpEntity<>(authValidationRequest, headers);

        ResponseEntity<Boolean> response = restTemplateConfig.restTemplate().postForEntity("http://localhost:8085/api/auth/validate-client", entity, Boolean.class);

        if (!response.getStatusCode().is2xxSuccessful() || Boolean.FALSE.equals(response.getBody())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка при проверке клиента");
        }

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

        ResponseEntity<Void> withdrawResponse  = restTemplateConfig.restTemplate().postForEntity("http://localhost:8081/api/core/debit-accounts/" + request.getDebitAccountId() + "/withdraw", withdrawBody, Void.class);

        if (!withdrawResponse.getStatusCode().is2xxSuccessful()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ошибка списания средств с дебетого счета");
        }

        BigDecimal newDebt = credit.getDebtAmount().subtract(request.getAmount());

        credit.setLastPaymentDate(LocalDate.now());

        if (newDebt.compareTo(BigDecimal.ZERO) <= 0 ){
            credit.setDebtAmount(BigDecimal.ZERO);
            credit.setCreditStatus(CreditStatus.CLOSED);

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
                .amount(request.getAmount())
                .comment("Кредит " + credit.getId() + " пополнен на сумму " + request.getAmount())
                .operationType(OperationType.REPAYMENT)
                .build();

        creditOperationHistoryRepository.save(history);
    }
}
