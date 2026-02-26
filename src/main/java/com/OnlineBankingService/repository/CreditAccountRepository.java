package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

    List<CreditAccount> findByClientId(Long clientId);

    Optional<CreditAccount> findByIdAndClientId(UUID id, Long clientId);

    @Query(value = """
      UPDATE credit_account
      SET balance = balance + :delta
      WHERE id = :id
        AND status = 'OPEN'
        AND (balance + :delta) >= 0
      RETURNING balance
      """, nativeQuery = true)
    BigDecimal applyDeltaReturningBalance(@Param("id") UUID id,
                                          @Param("delta") BigDecimal delta);

    @Query(value = """
      UPDATE credit_account
      SET status = 'CLOSED'
      WHERE id = :id
        AND status = 'OPEN'
        AND balance = 0
      RETURNING id
      """, nativeQuery = true)
    Long closeIfZeroBalance(@Param("id") UUID id);
}
