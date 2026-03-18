package com.OnlineBankingService.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateCreditRatingRequest {
    private UUID userId;
    private Integer creditRating;
}
