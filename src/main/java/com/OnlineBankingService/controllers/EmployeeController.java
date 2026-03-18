package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.configs.EmployeeClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeClient employeeClient;

    public EmployeeController(EmployeeClient employeeClient) {
        this.employeeClient = employeeClient;
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeeClient.getAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable UUID id) {
        return employeeClient.getById(id);
    }

    @PostMapping
    public Employee create(@RequestBody CreateEmployeeDto employee, @RequestHeader("Authorization") String authorization) {
        return employeeClient.create(employee, authorization);
    }

    @PostMapping("/test")
    public Employee createTest(@RequestBody CreateEmployeeDto employee) {
        return employeeClient.createTest(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Employee employee) {
        return employeeClient.update(authorization, id, employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, @RequestHeader("Authorization") String authorization) {
        employeeClient.delete(id, authorization);
    }

    @PatchMapping("/{id}/lock")
    public Employee lock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return employeeClient.lock(id, token);
    }

    @PatchMapping("/{id}/unlock")
    public Employee unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return employeeClient.unlock(id, token);
    }
}
