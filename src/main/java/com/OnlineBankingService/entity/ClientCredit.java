package com.OnlineBankingService.entity;

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
@Table(name = "client_credit")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCredit {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "credit_tariff_id",nullable = false)
    private CreditTariff creditTariffId;

    private UUID clientId;
    private LocalDate issueData;
    private LocalTime issueTime;
    private BigDecimal creditAmount;
    private BigDecimal debtAmount;
}
