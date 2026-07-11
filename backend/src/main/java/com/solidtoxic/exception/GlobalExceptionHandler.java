package com.solidtoxic.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Centralised error mapping — never exposes SQL or schema details (Req 9-2).
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── Bean Validation (@Valid on DTOs) ─────────────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleBeanValidation(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return errorBody(HttpStatus.BAD_REQUEST, errors);
    }

    // ── Business validation (Service layer) ──────────────────────────────────
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(ValidationException ex) {
        return errorBody(HttpStatus.BAD_REQUEST, ex.getErrors());
    }

    // ── Duplicate primary key ─────────────────────────────────────────────────
    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<Map<String, Object>> handleDuplicate(DuplicateKeyException ex) {
        return errorBody(HttpStatus.CONFLICT, List.of("A record with that identifier already exists."));
    }

    // ── Record not found ──────────────────────────────────────────────────────
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return errorBody(HttpStatus.NOT_FOUND, List.of(ex.getMessage()));
    }

    // ── Generic database error (never leak schema details) ────────────────────
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("A database error occurred. Please contact support."));
    }

    // ── Catch-all ─────────────────────────────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("An unexpected error occurred. Please try again."));
    }

    // ── Helper ────────────────────────────────────────────────────────────────
    private ResponseEntity<Map<String, Object>> errorBody(HttpStatus status, List<String> errors) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", status.value());
        body.put("errors", errors);
        return ResponseEntity.status(status).body(body);
    }
}
