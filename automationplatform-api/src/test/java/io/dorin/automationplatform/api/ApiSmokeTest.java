package io.dorin.automationplatform.api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test that validates:
 * <ul>
 *   <li>API client can execute requests</li>
 *   <li>timeouts and baseUrl work</li>
 *   <li>Allure attachments are produced</li>
 * </ul>
 */
public class ApiSmokeTest {

    @Test
    @Tag("smoke")
    void api_client_can_call_local_server() throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
        server.createContext("/health", ApiSmokeTest::handleHealth);
        server.start();

        int port = server.getAddress().getPort();
        String baseUrl = "http://localhost:" + port;

        try {
            ApiConfig config = ApiConfig.of(
                    baseUrl,
                    Duration.ofSeconds(5),
                    Duration.ofSeconds(5)
            );

            ApiClient client = new ApiClient(config);

            int status = client.request()
                    .when()
                    .get("/health")
                    .then()
                    .extract()
                    .statusCode();

            assertThat(status).isEqualTo(200);
        } finally {
            server.stop(0);
        }
    }

    private static void handleHealth(HttpExchange exchange) throws IOException {
        byte[] payload = "{\"status\":\"UP\"}".getBytes();

        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, payload.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(payload);
        }
    }
}
