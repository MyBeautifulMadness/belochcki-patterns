package com.OnlineBankingService.dtos;

import lombok.Data;

@Data
public class CreateEmployeeDto {
    public String name;
    public String login;
    public String password;
}