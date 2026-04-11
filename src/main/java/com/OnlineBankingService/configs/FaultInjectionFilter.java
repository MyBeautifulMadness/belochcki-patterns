package com.OnlineBankingService.configs;

import com.OnlineBankingService.metrics.RequestMetrics;
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

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI();

        if (path.contains("/actuator") || path.contains("/health")) {
            chain.doFilter(req, res);
            return;
        }

        String internal = request.getHeader("X-Internal-Call");
        if ("true".equals(internal)) {
            chain.doFilter(req, res);
            return;
        }

        if (RequestMetrics.isCircuitOpen()) {
            response.setStatus(503);
            response.getWriter().write("Circuit Breaker OPEN");
            return;
        }

        int minute = LocalDateTime.now().getMinute();
        int errorRate = (minute % 2 == 0) ? 70 : 30;

        if (random.nextInt(100) < errorRate) {
            response.setStatus(500);
            return;
        }

        chain.doFilter(req, res);
    }
}