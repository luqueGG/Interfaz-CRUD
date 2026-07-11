package com.solidtoxic.gui.client;

/**
 * Thrown when the backend cannot be reached (connection refused, timeout, etc.).
 * Req 8-2, 8-3, 9-1.
 */
public class BackendUnavailableException extends RuntimeException {
    public BackendUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
