package com.OnlineBankingService.configs;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

public class FeignConfig {

    @Bean
    public RequestInterceptor internalCallInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-Internal-Call", "true");
        };
    }
}
