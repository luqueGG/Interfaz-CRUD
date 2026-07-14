package com.solidtoxic.exception;

import org.postgresql.util.PSQLException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
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

    // ── Database error: distinguish "business rule" (trigger/procedure) from
    //    real infrastructure failures. Never leak raw SQL/schema details, but
    //    DO show the friendly message our own triggers/procedures raise. ─────
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> handleDataAccess(DataAccessException ex) {
        String businessMessage = extractTriggerMessage(ex);
        if (businessMessage != null) {
            return errorBody(HttpStatus.BAD_REQUEST, List.of(businessMessage));
        }
        return errorBody(HttpStatus.INTERNAL_SERVER_ERROR,
                List.of("A database error occurred. Please contact support."));
    }

    /**
     * Walks the exception cause chain looking for a PostgreSQL error raised by
     * one of our triggers or procedures via RAISE EXCEPTION (SQLSTATE P0001,
     * the default "raise_exception" code) and returns its clean message, or
     * null if this isn't one of ours (so the caller falls back to a generic
     * message instead of leaking real DB/infra errors).
     */
    private String extractTriggerMessage(Throwable ex) {
        Throwable current = ex;
        while (current != null) {
            if (current instanceof PSQLException psql) {
                String sqlState = psql.getSQLState();
                if ("P0001".equals(sqlState)) {
                    String msg = psql.getServerErrorMessage() != null
                            ? psql.getServerErrorMessage().getMessage()
                            : psql.getMessage();
                    return msg != null ? msg.replaceFirst("^ERROR:\\s*", "").trim() : null;
                }
                return null; // some other SQL error (syntax, connection, etc.) -> stay generic
            }
            if (current instanceof SQLException sql) {
                if ("P0001".equals(sql.getSQLState())) {
                    return sql.getMessage() != null
                            ? sql.getMessage().replaceFirst("^ERROR:\\s*", "").trim()
                            : null;
                }
                return null;
            }
            current = current.getCause();
        }
        return null;
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
