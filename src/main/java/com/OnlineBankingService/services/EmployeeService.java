package com.OnlineBankingService.services;

import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.entities.Status;
import com.OnlineBankingService.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee create(Employee employee) {
        employee.id = UUID.randomUUID();
        employee.status = Status.UNLOCKED;
        return employeeRepository.save(employee);
    }

    public Employee update(UUID id, Employee updated) {
        Employee employee = findById(id);
        employee.login = updated.login;
        employee.password = updated.password;
        employee.status = updated.status;
        return employeeRepository.save(employee);
    }

    public void delete(UUID id) {
        employeeRepository.deleteById(id);
    }

    public Employee lock(UUID id) {
        Employee employee = findById(id);
        employee.status = Status.LOCKED;
        return employeeRepository.save(employee);
    }

    public Employee unlock(UUID id) {
        Employee employee = findById(id);
        employee.status = Status.UNLOCKED;
        return employeeRepository.save(employee);
    }
}