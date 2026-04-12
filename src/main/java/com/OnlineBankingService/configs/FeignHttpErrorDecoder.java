package com.OnlineBankingService.configs;

import com.OnlineBankingService.controllers.ApiErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Request;
import feign.Response;
import feign.RetryableException;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

public class FeignHttpErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        ApiErrorDto apiError = extractError(response);
        HttpStatus status = HttpStatus.resolve(response.status());

        if (status == null) {
            return new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unknown error"
            );
        }

        String message = (apiError != null && apiError.message != null)
                ? apiError.message
                : "Unknown error";

        if (status.value() == 503 && message.toLowerCase().contains("circuit breaker open")) {
            return new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    message
            );
        }

        if (status.is5xxServerError()) {
            Request request = response.request();

            return new RetryableException(
                    response.status(),
                    message,
                    request.httpMethod(),
                    null,
                    (Long) null,
                    request
            );
        }

        return defaultErrorDecoder.decode(methodKey, response);
    }

    private ApiErrorDto extractError(Response response) {
        try {
            if (response.body() == null) {
                return null;
            }

            byte[] bodyBytes = response.body().asInputStream().readAllBytes();

            return objectMapper.readValue(
                    new String(bodyBytes, StandardCharsets.UTF_8),
                    ApiErrorDto.class
            );
        } catch (Exception e) {
            return null;
        }
    }
}