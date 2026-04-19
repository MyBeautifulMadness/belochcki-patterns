package com.OnlineBankingService.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter
@Getter
@Entity
public class PushSubscription {

    @Id
    private String endpoint;

    private String p256dh;
    private String auth;
    private UUID userId;

}
