package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.AuthRequestDto;
import com.OnlineBankingService.dtos.AuthResponseDto;
import com.OnlineBankingService.dtos.TokenClientRequestDto;
import com.OnlineBankingService.dtos.TokenRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service", url = "http://localhost:8083/api/auth",configuration = FeignConfig.class)
public interface AuthClient {

    @PostMapping("/login")
    AuthResponseDto login(@RequestBody AuthRequestDto dto);
}
