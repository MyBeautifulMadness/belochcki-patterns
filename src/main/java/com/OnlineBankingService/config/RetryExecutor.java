package com.OnlineBankingService.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.server.ResponseStatusException;

import java.util.function.Supplier;

@Slf4j
@Component
public class RetryExecutor {

    private static final int MAX_ATTEMPTS = 3;
    private static final long INITIAL_DELAY_MS = 200;

    public <T> T execute(Supplier<T> action, String operationName) {
        long delay = INITIAL_DELAY_MS;
        Exception lastException = null;

        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                log.warn("Retry attempt {} for {}", attempt, operationName);
                return action.get();
            } catch (Exception e) {
                if (!isRetryable(e)) {
                    if (isCircuitBreakerOpen(e)) {
                        log.warn("Circuit Breaker OPEN for {}: {}", operationName, extractMessage(e));
                    } else {
                        log.warn("Non-retryable error for {}: {}", operationName, extractMessage(e));
                    }
                    throw e;
                }

                lastException = e;
                log.warn("Retryable error on attempt {} for {}: {}", attempt, operationName, e.getMessage());

                if (attempt == MAX_ATTEMPTS) {
                    break;
                }

                sleep(delay);
                delay *= 2;
            }
        }

        log.error("All retry attempts failed for {}", operationName, lastException);

        throw mapFinalException(operationName, lastException);
    }

    private boolean isCircuitBreakerOpen(Exception e) {
        if (e instanceof ResponseStatusException rse) {
            String reason = rse.getReason();
            return reason != null && reason.toLowerCase().contains("circuit breaker open");
        }
        return false;
    }

    private String extractMessage(Exception e) {
        if (e instanceof ResponseStatusException rse && rse.getReason() != null) {
            return rse.getReason();
        }
        return e.getMessage();
    }

    public void executeVoid(Runnable action, String operationName) {
        execute(() -> {
            action.run();
            return null;
        }, operationName);
    }

    private boolean isRetryable(Exception e) {
        if (e instanceof ResponseStatusException responseStatusException) {
            String reason = responseStatusException.getReason();
            if (reason != null && reason.toLowerCase().contains("circuit breaker open")) {
                return false;
            }
            return responseStatusException.getStatusCode().is5xxServerError();
        }

        if (e instanceof ResourceAccessException) {
            return true;
        }

        if (e instanceof HttpServerErrorException) {
            return true;
        }

        return false;
    }

    private ResponseStatusException mapFinalException(String operationName, Exception lastException) {
        if (lastException instanceof ResponseStatusException rse) {
            String reason = rse.getReason();

            if (reason != null && reason.toLowerCase().contains("circuit breaker open")) {
                return new ResponseStatusException(
                        HttpStatusCode.valueOf(503),
                        "Circuit Breaker OPEN: " + operationName,
                        lastException
                );
            }
        }

        return new ResponseStatusException(
                HttpStatusCode.valueOf(500),
                "Ошибка после исчерпания retry: " + operationName,
                lastException
        );
    }

    private void sleep(long delay) {
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Retry interrupted", e);
        }
    }
}
