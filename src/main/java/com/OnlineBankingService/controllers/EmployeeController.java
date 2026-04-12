package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.EmployeeClient;
import com.OnlineBankingService.configs.RetryExecutor;
import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.entities.Employee;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeClient employeeClient;
    private final RetryExecutor retryExecutor;

    public EmployeeController(EmployeeClient employeeClient, RetryExecutor retryExecutor) {
        this.employeeClient = employeeClient;
        this.retryExecutor = retryExecutor;
    }

    @GetMapping
    public List<Employee> getAll() {
        return retryExecutor.execute(employeeClient::getAll, "gateway -> info get all employees");
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable UUID id) {
        return retryExecutor.execute(() -> employeeClient.getById(id), "gateway -> info get employee by id");
    }

    @PostMapping
    public Employee create(@RequestBody CreateEmployeeDto employee, @RequestHeader("Authorization") String authorization) {
        return retryExecutor.execute(() -> employeeClient.create(employee, authorization), "gateway -> info create employee");
    }

    @PostMapping("/test")
    public Employee createTest(@RequestBody CreateEmployeeDto employee) {
        return retryExecutor.execute(() -> employeeClient.createTest(employee), "gateway -> info create employee test");
    }

    @PutMapping("/{id}")
    public Employee update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Employee employee) {
        return retryExecutor.execute(() -> employeeClient.update(authorization, id, employee), "gateway -> info update employee");
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id, @RequestHeader("Authorization") String authorization) {
        retryExecutor.executeVoid(() -> employeeClient.delete(id, authorization), "gateway -> info delete employee");
    }

    @PutMapping("/{id}/lock")
    public Employee lock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return retryExecutor.execute(() -> employeeClient.lock(id, token), "gateway -> info lock employee");
    }

    @PutMapping("/{id}/unlock")
    public Employee unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return retryExecutor.execute(() -> employeeClient.unlock(id, token), "gateway -> info unlock employee");
    }

    @GetMapping("/token")
    public Employee getByToken(@RequestParam String token) {
        return retryExecutor.execute(() -> employeeClient.getByToken(token), "gateway -> info get employee by token");
    }
}
