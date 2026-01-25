package io.dorin.automationplatform.api;

import java.time.Duration;
import java.util.Optional;

/**
 * Centralized configuration for API tests.
 *
 * <p>
 * Reads configuration from system properties or environment variables
 * and provides safe defaults so API tests work out of the box.
 * </p>
 *
 * <p>
 * Supported inputs:
 * <ul>
 *   <li>-Dapi.baseUrl or API_BASE_URL</li>
 *   <li>-Dapi.connectTimeoutMs or API_CONNECT_TIMEOUT_MS</li>
 *   <li>-Dapi.readTimeoutMs or API_READ_TIMEOUT_MS</li>
 * </ul>
 * </p>
 */
public final class ApiConfig {

    private final String baseUrl;
    private final Duration connectTimeout;
    private final Duration readTimeout;

    private ApiConfig(String baseUrl, Duration connectTimeout, Duration readTimeout) {
        this.baseUrl = baseUrl;
        this.connectTimeout = connectTimeout;
        this.readTimeout = readTimeout;
    }

    /**
     * Creates an {@link ApiConfig} using system properties / environment variables.
     * <p>
     * If nothing is provided, sensible defaults are used.
     * </p>
     */
    public static ApiConfig fromSystem() {
        String baseUrl = firstNonBlank(
                System.getProperty("api.baseUrl"),
                System.getenv("API_BASE_URL")
        ).orElse("http://localhost:8080");

        Duration connectTimeout = Duration.ofMillis(parseLong(
                firstNonBlank(
                        System.getProperty("api.connectTimeoutMs"),
                        System.getenv("API_CONNECT_TIMEOUT_MS")
                ).orElse("5000")
        ));

        Duration readTimeout = Duration.ofMillis(parseLong(
                firstNonBlank(
                        System.getProperty("api.readTimeoutMs"),
                        System.getenv("API_READ_TIMEOUT_MS")
                ).orElse("15000")
        ));

        return new ApiConfig(baseUrl, connectTimeout, readTimeout);
    }

    /**
     * Explicit factory method, useful for tests and local servers.
     */
    public static ApiConfig of(String baseUrl, Duration connectTimeout, Duration readTimeout) {
        return new ApiConfig(baseUrl, connectTimeout, readTimeout);
    }

    /** Base URL for all API calls. */
    public String baseUrl() {
        return baseUrl;
    }

    /** Connection timeout used by the HTTP client. */
    public Duration connectTimeout() {
        return connectTimeout;
    }

    /** Read / socket timeout used by the HTTP client. */
    public Duration readTimeout() {
        return readTimeout;
    }

    // ---------- internal helpers ----------

    private static Optional<String> firstNonBlank(String... values) {
        for (String v : values) {
            if (v != null && !v.trim().isEmpty()) {
                return Optional.of(v.trim());
            }
        }
        return Optional.empty();
    }

    private static long parseLong(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid numeric value: " + value, e);
        }
    }
}
