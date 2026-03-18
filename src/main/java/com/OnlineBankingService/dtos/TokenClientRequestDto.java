package com.OnlineBankingService.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class TokenClientRequestDto {
    public String token;
    public UUID clientId;
}
