package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.AuthClient;
import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.dtos.TokenClientRequestDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthClient authClient;

    public AuthController(AuthClient authClient) {
        this.authClient = authClient;
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto dto) {
        return authClient.login(dto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody TokenRequestDto dto) {
        authClient.logout(dto);
    }

    @PostMapping("/validate")
    public boolean validate(@RequestBody TokenRequestDto dto) {
        return authClient.validate(dto);
    }

    @PostMapping("/validate-client")
    public boolean validateClientToken(@RequestBody TokenClientRequestDto dto) {
        return authClient.validateClient(dto);
    }

    @PostMapping("/validate-employee")
    public boolean validateEmployeeToken(@RequestBody TokenRequestDto token) {
        return authClient.validateEmployee(token);
    }
}
