package com.OnlineBankingService.repository;

import com.OnlineBankingService.entity.ProcessedCommand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedCommandRepository extends JpaRepository<ProcessedCommand, UUID> {
}
