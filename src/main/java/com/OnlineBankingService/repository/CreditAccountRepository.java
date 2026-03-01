package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.CreditAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CreditAccountRepository extends JpaRepository<CreditAccount, UUID> {

    Optional<CreditAccount> findByClientId(UUID clientId);

    boolean existsByIdAndClientId(UUID id, UUID clientId);
    boolean existsByClientId(UUID clientId);
    boolean existsByName(String name);

    @Query(value = """
  UPDATE credit_account
  SET balance = balance + :delta
  WHERE client_id = :clientId
    AND status = 'OPEN'
  RETURNING balance
  """, nativeQuery = true)
    BigDecimal addToBalanceByClientIdOpen(@Param("clientId") UUID clientId,
                                          @Param("delta") BigDecimal delta);

    @Query(value = """
  UPDATE credit_account
  SET status = 'OPEN',
      balance = balance + :delta
  WHERE client_id = :clientId
    AND status = 'CLOSED'
  RETURNING balance
  """, nativeQuery = true)
    BigDecimal openAndAddToBalanceByClientId(@Param("clientId") UUID clientId,
                                             @Param("delta") BigDecimal delta);

    @Modifying
    @Query(value = """
  UPDATE credit_account
  SET status = 'CLOSED'
  WHERE client_id = :clientId
    AND status = 'OPEN'
  """, nativeQuery = true)
    int closeByClientId(@Param("clientId") UUID clientId);

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
}
