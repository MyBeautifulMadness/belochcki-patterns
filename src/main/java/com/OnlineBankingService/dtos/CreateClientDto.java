package com.OnlineBankingService.dtos;

import lombok.Data;

@Data
public class CreateClientDto {
    public String name;
    public String login;
    public String password;
}
