package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.CreateUserSettingsDtoRequest;
import com.OnlineBankingService.dtos.UserSettingsDtoResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "credit-service", url = "http://localhost:8089/api/userSettings", configuration = FeignConfig.class)
public interface CreditClient {
    @PostMapping
    UserSettingsDtoResponse create(@Valid @RequestBody CreateUserSettingsDtoRequest request);
}
