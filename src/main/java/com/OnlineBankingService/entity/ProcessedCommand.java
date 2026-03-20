package com.OnlineBankingService.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_command")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProcessedCommand {

    @Id
    @Column(name = "operation_id", nullable = false)
    private UUID operationId;

    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;
}
