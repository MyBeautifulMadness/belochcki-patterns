package com.OnlineBankingService.config;

import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.CreditTariff;
import com.OnlineBankingService.entity.enums.CreditStatus;
import com.OnlineBankingService.repository.ClientCreditRepository;
import com.OnlineBankingService.repository.CreditTariffRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccrualInterestForCreditService {

    private final ClientCreditRepository clientCreditRepository;
    private final CreditTariffRepository creditTariffRepository;

    @Scheduled(fixedRate = 3600000) //60000
    @Transactional
    public void accualInterest(){

        //log.info("Началось начисление процентов по кредиту");

        List<ClientCredit> credits = clientCreditRepository.findAllByCreditStatus(CreditStatus.OPEN);

        for (ClientCredit credit : credits){

            if (credit.getDebtAmount().compareTo(BigDecimal.ZERO) <= 0){
                continue;
            }

            CreditTariff tariff = creditTariffRepository.findById(credit.getCreditTariffId().getId()).orElseThrow(() -> new RuntimeException("Тариф не найден"));

            BigDecimal interestRate = tariff.getInterestRate();

            BigDecimal interestAmount = credit.getDebtAmount().multiply(interestRate).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP); //округляю в большую сторону, чтобы избежать ошибок)

            BigDecimal newDebt = credit.getDebtAmount().add(interestAmount);
            credit.setDebtAmount(newDebt);

            //log.info("Задолженность кредита {} составляет {} рублей", credit.getId(), newDebt);
        }

        //log.info("Процесс начисления закончен");
    }
}
