package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.services.EmployeeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public List<Employee> getAll() {
        return employeeService.findAll();
    }

    @GetMapping("/{id}")
    public Employee getById(@PathVariable UUID id) {
        return employeeService.findById(id);
    }

    @PostMapping
    public Employee create(@RequestBody CreateEmployeeDto employee) {
        return employeeService.create(employee);
    }

    @PutMapping("/{id}")
    public Employee update(@PathVariable UUID id, @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        employeeService.delete(id);
    }

    @PatchMapping("/{id}/lock")
    public Employee lock(@PathVariable UUID id, @RequestBody String token) {
        return employeeService.lock(id, token);
    }

    @PatchMapping("/{id}/unlock")
    public Employee unlock(@PathVariable UUID id, @RequestBody String token) {
        return employeeService.unlock(id, token);
    }
}
