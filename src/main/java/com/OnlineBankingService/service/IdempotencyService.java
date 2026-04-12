package com.OnlineBankingService.service;

import com.OnlineBankingService.entity.IdempotencyRecord;
import com.OnlineBankingService.repository.IdempotencyRecordRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final IdempotencyRecordRepository idempotencyRecordRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    public <T> T execute(
            String idempotencyKey,
            String operationName,
            Class<T> responseType,
            Supplier<T> action
    ) {
        validateKey(idempotencyKey);

        Optional<IdempotencyRecord> existing = idempotencyRecordRepository.findById(idempotencyKey);
        if (existing.isPresent()) {
            return deserialize(existing.get().getResponseBody(), responseType);
        }

        T result = action.get();

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .operationName(operationName)
                .responseStatus(200)
                .responseBody(serialize(result))
                .createdAt(LocalDateTime.now())
                .build();

        try {
            idempotencyRecordRepository.save(record);
        } catch (DataIntegrityViolationException e) {
            IdempotencyRecord saved = idempotencyRecordRepository.findById(idempotencyKey)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to resolve idempotent request after duplicate key"
                    ));
            return deserialize(saved.getResponseBody(), responseType);
        }

        return result;
    }

    @Transactional
    public void executeVoid(
            String idempotencyKey,
            String operationName,
            Runnable action
    ) {
        validateKey(idempotencyKey);

        Optional<IdempotencyRecord> existing = idempotencyRecordRepository.findById(idempotencyKey);
        if (existing.isPresent()) {
            return;
        }

        action.run();

        IdempotencyRecord record = IdempotencyRecord.builder()
                .idempotencyKey(idempotencyKey)
                .operationName(operationName)
                .responseStatus(200)
                .responseBody(null)
                .createdAt(LocalDateTime.now())
                .build();

        try {
            idempotencyRecordRepository.save(record);
        } catch (DataIntegrityViolationException e) {
            idempotencyRecordRepository.findById(idempotencyKey)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            "Failed to resolve idempotent void request after duplicate key"
                    ));
        }
    }

    private void validateKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Idempotency-Key header is required"
            );
        }
    }

    private String serialize(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to serialize idempotent response"
            );
        }
    }

    private <T> T deserialize(String json, Class<T> responseType) {
        try {
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, responseType);
        } catch (JsonProcessingException e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Failed to deserialize idempotent response"
            );
        }
    }
}