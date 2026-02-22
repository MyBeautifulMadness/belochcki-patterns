package com.OnlineBankingService.entity;

import com.OnlineBankingService.entity.enums.OperationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "credit_operation_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditOperationHistory {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "client_credit_id", nullable = false)
    private ClientCredit clientCreditId;

    private LocalDate date;
    private LocalTime time;
    private BigDecimal amount;
    private String comment;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type", nullable = false)
    private OperationType operationType;
}
