package com.OnlineBankingService.services;

import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.entities.Status;
import com.OnlineBankingService.repositories.ClientRepository;
import com.OnlineBankingService.repositories.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final ClientRepository clientRepository;
    private final EmployeeRepository employeeRepository;
    private final JwtService jwtService;

    public AuthService(ClientRepository clientRepository,
                       EmployeeRepository employeeRepository,
                       JwtService jwtService) {
        this.clientRepository = clientRepository;
        this.employeeRepository = employeeRepository;
        this.jwtService = jwtService;
    }

    public AuthResponseDto login(AuthRequestDto dto) {

        if ("EMPLOYEE".equalsIgnoreCase(dto.userType)) {

            Employee employee = employeeRepository.findByLogin(dto.login)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            if (!employee.password.equals(dto.password)) {
                throw new RuntimeException("Wrong password");
            }

            String token = jwtService.generateToken(employee.id, employee.login, "EMPLOYEE");
            employee.token = token;
            employeeRepository.save(employee);

            AuthResponseDto res = new AuthResponseDto();
            res.token = token;
            res.userId = employee.id;
            res.userType = "EMPLOYEE";
            return res;
        }

        if ("CLIENT".equalsIgnoreCase(dto.userType)) {

            Client client = clientRepository.findByLogin(dto.login)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            if (!client.password.equals(dto.password)) {
                throw new RuntimeException("Wrong password");
            }

            String token = jwtService.generateToken(client.id, client.login, "CLIENT");
            client.token = token;
            clientRepository.save(client);

            AuthResponseDto res = new AuthResponseDto();
            res.token = token;
            res.userId = client.id;
            res.userType = "CLIENT";
            return res;
        }

        throw new RuntimeException("Unknown userType");
    }

    public void logout(String token) {

        employeeRepository.findByToken(token).ifPresent(e -> {
            e.token = null;
            employeeRepository.save(e);
        });

        clientRepository.findByToken(token).ifPresent(c -> {
            c.token = null;
            clientRepository.save(c);
        });
    }

    public Object validateToken(String token) {

        final String vtoken = token.replace("Bearer ", "");
        if (jwtService.isTokenExpired(vtoken)) {
            logout(vtoken);
            throw new RuntimeException("Token expired");
        }

        return employeeRepository.findByToken(vtoken)
                .<Object>map(e -> {
                    if (e.status == Status.LOCKED) throw new RuntimeException("Employee locked");
                    return e;
                })
                .orElseGet(() ->
                        clientRepository.findByToken(vtoken)
                                .map(c -> {
                                    if (c.status == Status.LOCKED) throw new RuntimeException("Client locked");
                                    return c;
                                })
                                .orElseThrow(() -> new RuntimeException("Token not found"))
                );
    }

    public boolean validateTokenForClient(String token, UUID clientId) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        token = token.replace("Bearer ", "");
        if (jwtService.isTokenExpired(token)) {
            client.token = null;
            clientRepository.save(client);
            throw new RuntimeException("Token expired");
        }

        UUID tokenUserId = jwtService.extractUserId(token);

        if (!tokenUserId.equals(client.id)) {
            throw new RuntimeException("Token does not belong to this client");
        }

        if (client.token == null || !client.token.equals(token)) {
            throw new RuntimeException("Token is not active for this client");
        }

        if (client.status == Status.LOCKED) {
            throw new RuntimeException("Client is locked");
        }

        return true;
    }

    public boolean validateEmployeeByToken(String token) {
        token = token.replace("Bearer ", "");
        if (jwtService.isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        UUID employeeId = jwtService.extractUserId(token);
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        if (employee.status == Status.LOCKED) {
            throw new RuntimeException("Employee is locked");
        }

        return true;
    }

    public boolean validateClientOrEmployee(String token, UUID clientId) {
        token = token.replace("Bearer ", "");
        boolean clientValid = false;
        boolean employeeValid = false;

        try {
            Client client = clientRepository.findById(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            if (!jwtService.isTokenExpired(token) &&
                    jwtService.extractUserId(token).equals(client.id) &&
                    token.equals(client.token) &&
                    client.status != Status.LOCKED) {

                clientValid = true;
            }
        } catch (RuntimeException e) {
        }

        try {
            UUID employeeId = jwtService.extractUserId(token);
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));

            if (!jwtService.isTokenExpired(token) &&
                    employee.status != Status.LOCKED) {

                employeeValid = true;
            }
        } catch (RuntimeException e) {
        }

        if (!clientValid && !employeeValid) {
            throw new RuntimeException("Token is invalid for both client and employee");
        }

        return true;
    }
}
