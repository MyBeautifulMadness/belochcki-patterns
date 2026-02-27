package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.CreditOperationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CreditOperationHistoryRepository extends JpaRepository<CreditOperationHistory, UUID>, JpaSpecificationExecutor<CreditOperationHistory> {
}
