package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.dtos.TokenClientRequestDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8082/api/auth")
public interface AuthClient {

    @PostMapping("/login")
    AuthResponseDto login(@RequestBody AuthRequestDto dto);

    @PostMapping("/logout")
    void logout(@RequestBody TokenRequestDto dto);

    @PostMapping("/validate")
    boolean validate(@RequestBody TokenRequestDto dto);

    @PostMapping("/validate-client")
    boolean validateClient(
            @RequestBody TokenClientRequestDto dto
    );

    @PostMapping("/validate-employee")
    boolean validateEmployee(
            @RequestBody TokenRequestDto dto
    );
}
