package com.OnlineBankingService.configs;

import feign.RequestInterceptor;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor traceInterceptor() {
        return template -> {

            String traceId = MDC.get("traceId");
            if (traceId != null) {
                template.header("X-Trace-Id", traceId);
            }

            template.header("X-Internal-Call", "true");

            String spanId = java.util.UUID.randomUUID().toString();
            template.header("X-Span-Id", spanId);
        };
    }
}