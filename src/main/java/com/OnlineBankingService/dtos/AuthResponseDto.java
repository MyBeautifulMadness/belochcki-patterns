package com.OnlineBankingService.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthResponseDto {
    public String token;
    public UUID userId;
    public String userType;
}
