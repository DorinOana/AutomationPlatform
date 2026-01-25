package io.dorin.automationplatform.api;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

/**
 * Opinionated API client wrapper over RestAssured.
 *
 * <p>
 * Provides:
 * <ul>
 *   <li>centralized base URL</li>
 *   <li>timeouts</li>
 *   <li>JSON defaults</li>
 *   <li>automatic Allure request/response attachments</li>
 * </ul>
 * </p>
 */
public final class ApiClient {

    private final ApiConfig config;

    public ApiClient(ApiConfig config) {
        this.config = config;
    }

    /**
     * Creates a client using configuration resolved from system properties.
     */
    public static ApiClient defaultClient() {
        return new ApiClient(ApiConfig.fromSystem());
    }

    /**
     * Creates a fully configured {@link RequestSpecification}
     * ready for execution.
     */
    public RequestSpecification request() {
        RestAssuredConfig raConfig = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", (int) config.connectTimeout().toMillis())
                        .setParam("http.socket.timeout", (int) config.readTimeout().toMillis())
                );

        return RestAssured.given()
                .config(raConfig)
                .baseUri(config.baseUrl())
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new ApiAllureFilter());
    }
}
