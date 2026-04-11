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

            String traceId = MDC.get("traceId");
            if (traceId == null) traceId = "NO_TRACE";

            String log = "TRACE=" + traceId +
                    " METHOD=" + request.getMethod() +
                    " PATH=" + request.getRequestURI() +
                    " STATUS=" + status +
                    " TIME=" + time + "ms" +
                    " ERROR_RATE=" + String.format("%.2f", RequestMetrics.getErrorRate()) + "%";

            System.out.println(log);

            try {
                LogSender.send("core-service", log);
            } catch (Exception ignored) {
            }
        }
    }
}
