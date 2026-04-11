package com.OnlineBankingService.configs;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Order(1)
@Component
public class TraceIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String traceId = request.getHeader("X-Trace-Id");

            if (traceId == null || traceId.isEmpty()) {
                traceId = UUID.randomUUID().toString();
            }

            MDC.put("traceId", traceId);
            response.setHeader("X-Trace-Id", traceId);

            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}