package com.OnlineBankingService.configs;

import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.entities.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "user-service", url = "http://localhost:8082",
        configuration = FeignConfig.class)
public interface UserClient {

    @GetMapping("/api/clients/{id}")
    Client getClientById(@PathVariable UUID id);

    @GetMapping("/api/clients/login")
    Client getClientByLogin(@RequestParam String login);

    @GetMapping("/api/clients/token")
    Client getClientByToken(@RequestParam String token);

    @PutMapping("/api/clients/{id}")
    Client updateClient(@PathVariable UUID id, @RequestBody Client client);


    @GetMapping("/api/employees/{id}")
    Employee getEmployeeById(@PathVariable UUID id);

    @GetMapping("/api/employees/login")
    Employee getEmployeeByLogin(@RequestParam String login);

    @GetMapping("/api/employees/token")
    Employee getEmployeeByToken(@RequestParam String token);

    @PutMapping("/api/employees/{id}")
    Employee updateEmployee(@PathVariable UUID id, @RequestBody Employee employee);
}
