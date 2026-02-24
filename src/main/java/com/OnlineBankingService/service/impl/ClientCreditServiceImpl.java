package com.OnlineBankingService.service.impl;

import com.OnlineBankingService.config.RestTemplateConfig;
import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.dto.AuthValidationRequest;
import com.OnlineBankingService.entity.dto.ClientCreditResponse;
import com.OnlineBankingService.entity.dto.CreateClientCreditRequest;
import com.OnlineBankingService.entity.enums.CreditStatus;
import com.OnlineBankingService.repository.ClientCreditRepository;
import com.OnlineBankingService.repository.CreditTariffRepository;
import com.OnlineBankingService.service.ClientCreditService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientCreditServiceImpl implements ClientCreditService {

    private final ClientCreditRepository clientCreditRepository;
    private final CreditTariffRepository creditTariffRepository;
    private final RestTemplateConfig restTemplateConfig;

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
            throw new RuntimeException("Ошибка при проверке клиента");
        }

        CreditTariff tariff = creditTariffRepository.findById(request.getCreditTariffId()).orElseThrow(() -> new RuntimeException("Данный кредитный тариф не найден"));

        BigDecimal amount = request.getCreditAmount();
        if (amount.compareTo(tariff.getAmountFrom()) < 0 || amount.compareTo(tariff.getAmountTo()) > 0){
            throw new RuntimeException("Указанная сумма кредита не входит в диапазон выбранного тарифа");
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
    public List<ClientCreditResponse> getAllClientCredit(UUID clientId, UUID creditTariffId, BigDecimal creditAmountFrom, BigDecimal creditAmountTo, BigDecimal debtAmountFrom, BigDecimal debtAmountTo, String creditStatus, String sortBy, String direction){
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

        return clientCreditRepository
                .findAll(spec, sort)
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
    public ClientCreditResponse getByIdClientCredit(UUID id){

        ClientCredit credit = clientCreditRepository.findById(id).orElseThrow(() -> new RuntimeException("Данный кредит не найден"));

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
            throw new RuntimeException("Ошибка при проверке клиента");
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
}
