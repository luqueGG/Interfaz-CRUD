package com.solidtoxic.gui.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Singleton HTTP client for all REST calls to the Spring Boot backend.
 * Base URL defaults to http://localhost:8080 — set SOLIDTOXIC_API env var to override.
 * Req 8-3, 8-4, 9-1.
 */
public class ApiClient {

    private static final ApiClient INSTANCE = new ApiClient();

    private final HttpClient http;
    private final ObjectMapper mapper;
    private final String baseUrl;

    private ApiClient() {
        this.http = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
        this.mapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        String env = System.getenv("SOLIDTOXIC_API");
        this.baseUrl = (env != null && !env.isBlank()) ? env : "http://localhost:8080";
    }

    public static ApiClient getInstance() { return INSTANCE; }

    // ── GET ────────────────────────────────────────────────────────────────────
    public ApiResponse get(String path) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();
        return send(req);
    }

    // ── POST ───────────────────────────────────────────────────────────────────
    public ApiResponse post(String path, Object body) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .POST(jsonBody(body))
                .build();
        return send(req);
    }

    // ── PUT ────────────────────────────────────────────────────────────────────
    public ApiResponse put(String path, Object body) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .PUT(jsonBody(body))
                .build();
        return send(req);
    }

    // ── PATCH ──────────────────────────────────────────────────────────────────
    public ApiResponse patch(String path, Object body) {
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + path))
                .timeout(Duration.ofSeconds(10))
                .header("Content-Type", "application/json")
                .method("PATCH", jsonBody(body))
                .build();
        return send(req);
    }

    // ── Helpers ────────────────────────────────────────────────────────────────
    private ApiResponse send(HttpRequest req) {
        try {
            HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
            return new ApiResponse(resp.statusCode(), resp.body());
        } catch (ConnectException e) {
            throw new BackendUnavailableException("Cannot connect to backend at " + baseUrl, e);
        } catch (IOException | InterruptedException e) {
            throw new BackendUnavailableException("Communication error: " + e.getMessage(), e);
        }
    }

    private HttpRequest.BodyPublisher jsonBody(Object body) {
        try {
            return HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body));
        } catch (IOException e) {
            throw new RuntimeException("Failed to serialize request body", e);
        }
    }

    public ObjectMapper getMapper() { return mapper; }
}
