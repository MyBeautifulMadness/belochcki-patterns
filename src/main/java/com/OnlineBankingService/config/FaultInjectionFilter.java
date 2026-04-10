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
        if (path.contains("/actuator") || path.contains("/health")) {
            filterChain.doFilter(request, response);
            return;
        }

        String internalHeader = request.getHeader("X-Internal-Call");

        boolean isInternalCall = "true".equals(internalHeader);

        if (isInternalCall) {
            filterChain.doFilter(request, response);
            return;
        }

        int minute = LocalDateTime.now().getMinute();

        int errorRate = (minute % 2 == 0) ? 70 : 30;

        int roll = random.nextInt(100); // 0..99

        if (roll < errorRate) {
            response.sendError(500, "Simulated service failure");
            return;
        }

        filterChain.doFilter(request, response);
    }
}