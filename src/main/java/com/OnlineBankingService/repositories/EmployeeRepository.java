package com.OnlineBankingService.repositories;

import com.OnlineBankingService.entities.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {
    Optional<Employee> findByLogin(String login);
    Optional<Employee> findByToken(String token);
}
