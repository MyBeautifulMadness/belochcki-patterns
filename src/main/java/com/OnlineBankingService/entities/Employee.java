package com.OnlineBankingService.entities;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    public UUID id;

    @Column(nullable = false)
    public String name;

    @Column(unique = true, nullable = false)
    public String login;

    @Column(nullable = false)
    public String password;

    @Enumerated(EnumType.STRING)
    public Status status;

    @Column(length = 1000)
    public String token;
}