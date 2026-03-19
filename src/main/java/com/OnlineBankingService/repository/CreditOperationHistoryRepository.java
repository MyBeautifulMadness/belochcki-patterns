package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.CreditOperationHistory;
import com.OnlineBankingService.entity.enums.OperationType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.UUID;

public interface CreditOperationHistoryRepository extends JpaRepository<CreditOperationHistory, UUID>, JpaSpecificationExecutor<CreditOperationHistory> {
    List<CreditOperationHistory> findByClientCreditId_ClientIdAndOperationType(UUID clientId, OperationType operationType, Sort sort);
    List<CreditOperationHistory> findByClientCreditId_ClientIdAndClientCreditId_IdAndOperationType(UUID clientId, UUID creditId, OperationType operationType, Sort sort);
}
