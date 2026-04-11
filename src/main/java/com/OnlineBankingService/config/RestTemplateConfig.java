package com.OnlineBankingService.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.slf4j.MDC;

import java.util.UUID;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        restTemplate.getInterceptors().add((request, body, execution) -> {
            String traceId = MDC.get("traceId");
            if (traceId != null) {
                request.getHeaders().set("X-Trace-Id", traceId);
            }

            request.getHeaders().set("X-Internal-Call", "true");

            String spanId = UUID.randomUUID().toString();
            request.getHeaders().set("X-Span-Id", spanId);

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
