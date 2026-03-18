package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.MasterAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface MasterAccountRepository extends JpaRepository<MasterAccount, UUID> {

    @Query("select m from MasterAccount m")
    Optional<MasterAccount> findSingleMasterAccount();

    boolean existsByName(String name);

    @Query(value = """
        UPDATE master_account
        SET balance = balance + :delta
        WHERE id = :id
          AND status = 'OPEN'
          AND (balance + :delta) >= 0
        RETURNING balance
        """, nativeQuery = true)
    BigDecimal applyDeltaReturningBalance(@Param("id") UUID id,
                                          @Param("delta") BigDecimal delta);
}