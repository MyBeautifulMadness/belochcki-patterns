package com.OnlineBankingService.dtos;

import lombok.Data;

@Data
public class AuthRequestDto {
    public String login;
    public String password;
}