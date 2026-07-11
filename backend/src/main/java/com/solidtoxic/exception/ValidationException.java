package com.solidtoxic.exception;

import java.util.List;

/**
 * Thrown by the Service layer when business validation fails (Req 7).
 * Carries a list of human-readable field error messages.
 */
public class ValidationException extends RuntimeException {

    private final List<String> errors;

    public ValidationException(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public ValidationException(List<String> errors) {
        super(String.join("; ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
