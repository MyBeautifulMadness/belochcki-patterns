package com.OnlineBankingService.services;

import com.OnlineBankingService.configs.UserClient;
import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.entities.Employee;
import com.OnlineBankingService.entities.Status;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    private final UserClient userFeignClient;
    private final JwtService jwtService;
    private final NotificationService pushService;

    public AuthService(UserClient userFeignClient,
                       JwtService jwtService, NotificationService pushService) {
        this.userFeignClient = userFeignClient;
        this.jwtService = jwtService;
        this.pushService = pushService;
    }


    public AuthResponseDto login(AuthRequestDto dto) {

        List<String> roles = new ArrayList<>();
        UUID userId = null;

        Employee employee = null;
        Client client = null;

        try {
            employee = userFeignClient.getEmployeeByLogin(dto.login);
        } catch (feign.FeignException.NotFound e) {
            employee = null;
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Employee service unavailable"
            );
        }

        try {
            client = userFeignClient.getClientByLogin(dto.login);
        } catch (feign.FeignException.NotFound e) {
            client = null;
        } catch (feign.FeignException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_GATEWAY,
                    "Client service unavailable"
            );
        }

        boolean passwordMatched = false;

        if (employee != null) {
            if (employee.password.equals(dto.password)) {
                roles.add("EMPLOYEE");
                userId = employee.id;
                passwordMatched = true;
            }
        }

        if (client != null) {
            if (client.password.equals(dto.password)) {
                roles.add("CLIENT");
                userId = client.id;
                passwordMatched = true;
            }
        }

        if (employee == null && client == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "User not found"
            );
        }

        if (!passwordMatched) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Invalid password"
            );
        }

        String token = jwtService.generateToken(userId, dto.login, roles);

        AuthResponseDto responseDto = new AuthResponseDto();
        responseDto.setToken(token);
        responseDto.setUserId(userId);
        responseDto.setUserType(roles.contains("EMPLOYEE") ? "EMPLOYEE" : "CLIENT");

        pushService.sendToUser(userId, "{\"title\":\"Новая операция\",\"body\":\"Создана операция\"}");
        pushService.sendToAll("{\"title\":\"Операция\",\"body\":\"Новая операция в системе\"}");

        return responseDto;
    }


    public void logout(String token) {

        String vtoken = token.replace("Bearer ", "");

        String role = jwtService.extractRole(vtoken);
        UUID userId = jwtService.extractUserId(vtoken);

        if ("EMPLOYEE".equals(role)) {
            Employee employee = userFeignClient.getEmployeeById(userId);
            employee.token = null;
            userFeignClient.updateEmployee(userId, employee);
            return;
        }

        if ("CLIENT".equals(role)) {
            Client client = userFeignClient.getClientById(userId);
            client.token = null;
            userFeignClient.updateClient(userId, client);
            return;
        }

        throw new RuntimeException("Invalid role");
    }

    public Object validateToken(String token) {

        String vtoken = token.replace("Bearer ", "");

        if (jwtService.isTokenExpired(vtoken)) {
            logout(vtoken);
            throw new RuntimeException("Token expired");
        }

        String role = jwtService.extractRole(vtoken);
        UUID userId = jwtService.extractUserId(vtoken);

        if ("EMPLOYEE".equals(role)) {

            Employee employee = userFeignClient.getEmployeeById(userId);

            if (employee.status == Status.LOCKED) {
                throw new RuntimeException("Employee locked");
            }

            if (employee.token == null || !employee.token.equals(vtoken)) {
                throw new RuntimeException("Token not active");
            }

            return employee;
        }

        if ("CLIENT".equals(role)) {

            Client client = userFeignClient.getClientById(userId);

            if (client.status == Status.LOCKED) {
                throw new RuntimeException("Client locked");
            }

            if (client.token == null || !client.token.equals(vtoken)) {
                throw new RuntimeException("Token not active");
            }

            return client;
        }

        throw new RuntimeException("Invalid role");
    }

    public boolean validateTokenForClient(String token, UUID clientId) {

        String vtoken = token.replace("Bearer ", "");

        if (jwtService.isTokenExpired(vtoken)) {
            throw new RuntimeException("Token expired");
        }

        UUID tokenUserId = jwtService.extractUserId(vtoken);

        if (!tokenUserId.equals(clientId)) {
            throw new RuntimeException("Token does not belong to this client");
        }

        Client client = userFeignClient.getClientById(clientId);

        if (client.token == null || !client.token.equals(vtoken)) {
            throw new RuntimeException("Token is not active for this client");
        }

        if (client.status == Status.LOCKED) {
            throw new RuntimeException("Client is locked");
        }

        return true;
    }

    public boolean validateEmployeeByToken(String token) {

        String vtoken = token.replace("Bearer ", "");

        if (jwtService.isTokenExpired(vtoken)) {
            throw new RuntimeException("Token expired");
        }

        String role = jwtService.extractRole(vtoken);

        if (!"EMPLOYEE".equals(role)) {
            throw new RuntimeException("Not an employee token");
        }

        UUID employeeId = jwtService.extractUserId(vtoken);

        Employee employee = userFeignClient.getEmployeeById(employeeId);

        if (employee.status == Status.LOCKED) {
            throw new RuntimeException("Employee is locked");
        }

        if (employee.token == null || !employee.token.equals(vtoken)) {
            throw new RuntimeException("Token not active");
        }

        return true;
    }

    public boolean validateClientOrEmployee(String token, UUID clientId) {

        String vtoken = token.replace("Bearer ", "");

        if (jwtService.isTokenExpired(vtoken)) {
            throw new RuntimeException("Token expired");
        }

        String role = jwtService.extractRole(vtoken);
        UUID userId = jwtService.extractUserId(vtoken);

        if ("CLIENT".equals(role)) {

            if (!userId.equals(clientId)) {
                throw new RuntimeException("Token does not belong to this client");
            }

            Client client = userFeignClient.getClientById(clientId);

            if (client.token == null || !client.token.equals(vtoken)) {
                throw new RuntimeException("Token not active");
            }

            if (client.status == Status.LOCKED) {
                throw new RuntimeException("Client locked");
            }

            return true;
        }

        if ("EMPLOYEE".equals(role)) {

            Employee employee = userFeignClient.getEmployeeById(userId);

            if (employee.status == Status.LOCKED) {
                throw new RuntimeException("Employee locked");
            }

            if (employee.token == null || !employee.token.equals(vtoken)) {
                throw new RuntimeException("Token not active");
            }

            return true;
        }

        throw new RuntimeException("Invalid role");
    }
}