package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.dtos.TokenClientRequestDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import com.OnlineBankingService.services.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponseDto login(@RequestBody AuthRequestDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/logout")
    public void logout(@RequestBody TokenRequestDto dto) {
        authService.logout(dto.token);
    }

    @PostMapping("/validate")
    public void validate(@RequestBody TokenRequestDto dto) {
        authService.validateToken(dto.token);
    }

    @PostMapping("/validate-client")
    public boolean validateClientToken(@RequestBody TokenClientRequestDto dto) {
        return authService.validateTokenForClient(dto.token, dto.clientId);
    }
}
