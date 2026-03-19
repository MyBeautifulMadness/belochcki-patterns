package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.entities.Employee;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "employee-service", url = "http://localhost:8082/api/employees")
public interface EmployeeClient {
    @GetMapping
    List<Employee> getAll();

    @GetMapping("/{id}")
    Employee getById(@PathVariable UUID id);

    @PostMapping
    Employee create(@RequestBody CreateEmployeeDto employee, @RequestHeader("Authorization") String authorization);

    @PostMapping("/test")
    Employee createTest(@RequestBody CreateEmployeeDto employee);

    @PutMapping("/{id}")
    Employee update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Employee employee);

    @DeleteMapping("/{id}")
    void delete(@PathVariable UUID id, @RequestHeader("Authorization") String authorization);

    @PatchMapping("/{id}/lock")
    Employee lock(@PathVariable UUID id, @RequestHeader("Authorization") String token);

    @PatchMapping("/{id}/unlock")
    Employee unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token);

    @GetMapping("/token")
    Employee getByToken(@RequestParam String token);
}
