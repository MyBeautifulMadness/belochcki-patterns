package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.CreditTariff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CreditTariffRepository extends JpaRepository<CreditTariff, UUID> {
}
