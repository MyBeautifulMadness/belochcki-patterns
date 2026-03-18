package com.OnlineBankingService.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Data
public class Client {

    public UUID id;
    public String name;
    public String login;
    public String password;

    @Enumerated(EnumType.STRING)
    public Status status;
    public String token;
}
