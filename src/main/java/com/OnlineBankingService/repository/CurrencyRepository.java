package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, String> {

    List<Currency> findByIsActiveTrue();

    boolean existsByCode(String code);
}
