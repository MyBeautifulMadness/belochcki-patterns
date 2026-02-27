package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.ClientCredit;
import com.OnlineBankingService.entity.enums.CreditStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface ClientCreditRepository extends JpaRepository<ClientCredit, UUID>, JpaSpecificationExecutor<ClientCredit> {
    List<ClientCredit> findByClientId(UUID clientId);
    List<ClientCredit> findAllByCreditStatus(CreditStatus creditStatus);
}
