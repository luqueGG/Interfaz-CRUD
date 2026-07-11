package com.solidtoxic.gui.client;

/**
 * Wraps an HTTP response: status code and raw body string.
 */
public class ApiResponse {
    private final int statusCode;
    private final String body;

    public ApiResponse(int statusCode, String body) {
        this.statusCode = statusCode;
        this.body = body;
    }

    public int getStatusCode() { return statusCode; }
    public String getBody() { return body; }

    public boolean isSuccess() { return statusCode >= 200 && statusCode < 300; }
}
