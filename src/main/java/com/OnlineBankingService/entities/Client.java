package com.OnlineBankingService.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {

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

    @Column(nullable = false)
    private Integer creditRating = 100;
}
