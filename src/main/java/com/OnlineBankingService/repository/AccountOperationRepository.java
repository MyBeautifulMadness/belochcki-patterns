package com.OnlineBankingService.repository;

import com.OnlineBankingService.domain.AccountType;
import com.OnlineBankingService.entity.AccountOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountOperationRepository extends JpaRepository<AccountOperation, UUID> {
    Page<AccountOperation> findByAccountId(UUID accountId, Pageable pageable);
}
