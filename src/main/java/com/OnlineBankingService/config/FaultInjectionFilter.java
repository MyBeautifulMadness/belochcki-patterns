package com.OnlineBankingService.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Random;

@Component
public class FaultInjectionFilter extends OncePerRequestFilter {

    private final Random random = new Random();

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String path = request.getRequestURI();
        String internalCallHeader = request.getHeader("X-Internal-Call");

        if (path.contains("/actuator")
                || path.contains("/health")
                || path.contains("/swagger-ui")
                || path.contains("/v3/api-docs")
                || path.contains("/swagger-resources")
                || path.contains("/webjars")
                || "true".equalsIgnoreCase(internalCallHeader)) {
            filterChain.doFilter(request, response);
            return;
        }

        int minute = LocalDateTime.now().getMinute();
        int errorRate = (minute % 2 == 0) ? 70 : 30;
        int roll = random.nextInt(100);

        if (roll < errorRate) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("""
                {
                  "status": 500,
                  "error": "Internal Server Error",
                  "message": "Simulated service failure"
                }
                """);
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}
