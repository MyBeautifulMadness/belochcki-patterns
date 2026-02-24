package com.OnlineBankingService.repositories;

import com.OnlineBankingService.entities.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientRepository extends JpaRepository<Client, UUID> {
    Optional<Client> findByLogin(String login);
    Optional<Client> findByToken(String token);
}
