package com.OnlineBankingService.configs;

import com.OnlineBankingService.controllers.ApiErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;

public class FeignHttpErrorDecoder implements ErrorDecoder {

    private final ObjectMapper objectMapper = new ObjectMapper();

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

        return new ResponseStatusException(status, message);
    }

    private ApiErrorDto extractError(Response response) {
        try {
            if (response.body() == null) return null;

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