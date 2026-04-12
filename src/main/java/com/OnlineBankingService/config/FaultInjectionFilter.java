package com.OnlineBankingService.config;

import com.OnlineBankingService.entity.dto.ErrorResponse;
import com.OnlineBankingService.metrics.RequestMetrics;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Order(3)
@Component
public class FaultInjectionFilter implements Filter {

    private final Random random = new Random();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        String internal = request.getHeader("X-Internal-Call");

        if (path.contains("/actuator") ||
                path.contains("/health") ||
                path.contains("/swagger-ui") ||
                path.contains("/v3/api-docs") ||
                path.contains("/swagger-resources") ||
                path.contains("/webjars") ||
                "true".equalsIgnoreCase(internal)) {
            chain.doFilter(request, response);
            return;
        }

        if ("true".equals(internal)) {
            chain.doFilter(req, res);
            return;
        }

        if (RequestMetrics.isCircuitOpen()) {
            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_SERVICE_UNAVAILABLE,
                    "Service Unavailable",
                    "Circuit Breaker OPEN"
            );
            return;
        }

        int minute = LocalDateTime.now().getMinute();
        int errorRate = (minute % 2 == 0) ? 70 : 30;

        if (random.nextInt(100) < errorRate) {
            writeErrorResponse(
                    response,
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Internal Server Error",
                    "Simulation of a system failure"
            );
            return;
        }

        chain.doFilter(req, res);
    }

    private void writeErrorResponse(HttpServletResponse response,
                                    int status,
                                    String error,
                                    String message) throws IOException {

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status)
                .error(error)
                .message(message)
                .build();

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
