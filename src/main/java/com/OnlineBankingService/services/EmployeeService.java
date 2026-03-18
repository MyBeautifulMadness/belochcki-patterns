package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.AuthClient;
import com.OnlineBankingService.dtos.CreateEmployeeDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.entities.Status;
import com.OnlineBankingService.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthClient authClient;

    public EmployeeService(EmployeeRepository employeeRepository,
                           AuthClient authFeignClient) {
        this.employeeRepository = employeeRepository;
        this.authClient = authFeignClient;
    }

    public List<Employee> findAll() {
        return employeeRepository.findAll();
    }

    public Employee findById(UUID id) {
        return employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee findByLogin(String login) {
        return employeeRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee findByToken(String token) {
        return employeeRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
    }

    public Employee create(CreateEmployeeDto dto, String token) {

        TokenRequestDto request = new TokenRequestDto();
        request.token = token;

        authClient.validateEmployee(request);

        Employee employee = new Employee();
        employee.id = UUID.randomUUID();
        employee.name = dto.name;
        employee.login = dto.login;
        employee.password = dto.password;
        employee.status = Status.UNLOCKED;

        return employeeRepository.save(employee);
    }

    public Employee createTest(CreateEmployeeDto dto) {
        Employee employee = new Employee();
        employee.id = UUID.randomUUID();
        employee.name = dto.name;
        employee.login = dto.login;
        employee.password = dto.password;
        employee.status = Status.UNLOCKED;
        return employeeRepository.save(employee);
    }

    public Employee update(UUID id, Employee updated, String token) {

        TokenRequestDto request = new TokenRequestDto();
        request.token = token;

        authClient.validateEmployee(request);

        Employee employee = findById(id);
        employee.login = updated.login;
        employee.name = updated.name;
        employee.password = updated.password;
        employee.status = updated.status;

        return employeeRepository.save(employee);
    }

    public void delete(UUID id, String token) {

        TokenRequestDto request = new TokenRequestDto();
        request.token = token;

        authClient.validateEmployee(request);

        employeeRepository.deleteById(id);
    }

    public Employee lock(UUID id, String token) {

        TokenRequestDto request = new TokenRequestDto();
        request.token = token;

        authClient.validateEmployee(request);

        Employee employee = findById(id);
        employee.status = Status.LOCKED;

        return employeeRepository.save(employee);
    }

    public Employee unlock(UUID id, String token) {

        TokenRequestDto request = new TokenRequestDto();
        request.token = token;

        authClient.validateEmployee(request);

        Employee employee = findById(id);
        employee.status = Status.UNLOCKED;

        return employeeRepository.save(employee);
    }
}