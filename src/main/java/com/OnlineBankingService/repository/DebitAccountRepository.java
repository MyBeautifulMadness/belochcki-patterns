package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.DebitAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DebitAccountRepository extends JpaRepository<DebitAccount, UUID> {

    List<DebitAccount> findByClientId(UUID clientId);

    boolean existsByIdAndClientId(UUID id, UUID clientId);
    boolean existsByName(String name);

    Optional<DebitAccount> findByIdAndClientId(UUID id, UUID clientId);

    @Query(value = """
      UPDATE debit_account
      SET balance = balance + :delta
      WHERE id = :id
        AND status = 'OPEN'
        AND (balance + :delta) >= 0
      RETURNING balance
      """, nativeQuery = true)
    BigDecimal applyDeltaReturningBalance(@Param("id") UUID id,
                                          @Param("clientId") UUID clientId,
                                          @Param("delta") BigDecimal delta);

    @Query(value = """
      UPDATE debit_account
      SET status = 'CLOSED'
      WHERE id = :id
        AND client_id = :clientId
        AND status = 'OPEN'
        AND balance = 0
      RETURNING id
      """, nativeQuery = true)
    UUID closeIfZeroBalance(@Param("id") UUID id,
                            @Param("clientId") UUID clientId);

    @Query(value = """
      UPDATE debit_account
      SET balance = balance + :delta
      WHERE id = :id
        AND status = 'OPEN'
        AND (balance + :delta) >= 0
      RETURNING balance
      """, nativeQuery = true)
    BigDecimal applyDeltaReturningBalanceWithoutClientCheck(@Param("id") UUID id,
                                                            @Param("delta") BigDecimal delta);
}
