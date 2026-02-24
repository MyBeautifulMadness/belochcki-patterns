package com.OnlineBankingService.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    boolean existsByLogin(String login);
    Optional<User> findByLogin(String login);
    Optional<User> findByToken(String token);
}
