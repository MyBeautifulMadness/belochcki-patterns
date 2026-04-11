package com.OnlineBankingService.config;

import com.OnlineBankingService.metrics.LogSender;
import com.OnlineBankingService.metrics.RequestMetrics;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Order(2)
@Component
public class LoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        long start = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {

            long time = System.currentTimeMillis() - start;

            int status = response.getStatus();

            RequestMetrics.record(status);

            double errorRate = RequestMetrics.getErrorRate();

            String traceId = MDC.get("traceId");
            if (traceId == null) {
                traceId = "NO_TRACE";
            }

            String log = String.format(
                    "TRACE=%s METHOD=%s PATH=%s STATUS=%d TIME=%dms ERROR_RATE=%.2f%%",
                    traceId,
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    time,
                    errorRate
            );

            System.out.println(log);

            try {
                LogSender.send("auth-service", log);
            } catch (Exception ignored) {
            }
        }
    }
}
