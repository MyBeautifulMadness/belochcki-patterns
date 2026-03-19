package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.services.EmployeeService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PostMapping("/test")
    public Employee createTest(@RequestBody CreateEmployeeDto employee) {
        return employeeService.createTest(employee);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PutMapping("/{id}")
    public Employee update(@PathVariable UUID id, @RequestBody Employee employee) {
        return employeeService.update(id, employee);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        employeeService.delete(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PatchMapping("/{id}/lock")
    public Employee lock(@PathVariable UUID id) {
        return employeeService.lock(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PatchMapping("/{id}/unlock")
    public Employee unlock(@PathVariable UUID id) {
        return employeeService.unlock(id);
    }

    @GetMapping("/login")
    public Employee getByLogin(@RequestParam String login) {
        return employeeService.findByLogin(login);
    }

    @GetMapping("/token")
    public Employee getByToken(@RequestParam String token) {
        return employeeService.findByToken(token);
    }
}
