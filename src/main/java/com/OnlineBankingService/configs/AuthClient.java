package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.TokenClientRequestDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "auth-service", url = "http://localhost:8083")
public interface AuthClient {

    @PostMapping("/api/auth/validate-client")
    boolean validateClient(@RequestBody TokenClientRequestDto dto);

    @PostMapping("/api/auth/validate-employee")
    boolean validateEmployee(@RequestBody TokenRequestDto dto);

    @PostMapping("/api/auth/validate-client-or-employee")
    boolean validateClientOrEmployee(
            @RequestHeader("Authorization") String token,
            @RequestParam UUID clientId
    );
}
