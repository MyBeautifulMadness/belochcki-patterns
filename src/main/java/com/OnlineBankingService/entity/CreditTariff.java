package com.OnlineBankingService.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Data
@Table(name = "credit_tariff")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreditTariff {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String name;
    private String description;
    private BigDecimal amountFrom;
    private BigDecimal amountTo;
    private BigDecimal interestRate;
}
