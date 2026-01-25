package io.dorin.automationplatform.api;

import io.dorin.automationplatform.core.allure.AllureAttachments;
import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * RestAssured filter that attaches API requests and responses to Allure reports.
 *
 * <p>
 * This filter is automatically applied by {@link ApiClient} so that:
 * <ul>
 *   <li>request method, URL, headers and body are attached</li>
 *   <li>response status, headers and body are attached</li>
 * </ul>
 * </p>
 *
 * <p>
 * Attachments are best-effort and never break the test execution.
 * </p>
 */
public final class ApiAllureFilter implements Filter {

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx
    ) {

        // Attach request details before execution
        Allure.addAttachment("API Request", "text/plain", buildRequestDump(requestSpec), ".txt");

        Response response = ctx.next(requestSpec, responseSpec);

        // Attach response details after execution
        Allure.addAttachment("API Response", "text/plain", buildResponseDump(response), ".txt");

        return response;
    }

    private static String buildRequestDump(FilterableRequestSpecification req) {
        String method = Optional.ofNullable(req.getMethod()).orElse("?");
        String uri = Optional.ofNullable(req.getURI()).orElse("?");

        StringBuilder sb = new StringBuilder();
        sb.append(method).append(" ").append(uri).append("\n");

        var headers = req.getHeaders();
        if (headers != null && headers.size() > 0) {
            sb.append("\nHeaders:\n").append(headers).append("\n");
        }

        Object body = req.getBody();
        if (body != null) {
            sb.append("\nBody:\n").append(body).append("\n");
        }

        return sb.toString();
    }

    private static String buildResponseDump(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append("Status: ").append(response.getStatusCode()).append("\n");

        try {
            var headers = response.getHeaders();
            if (headers != null && headers.size() > 0) {
                sb.append("\nHeaders:\n").append(headers).append("\n");
            }

            byte[] bytes = response.asByteArray();
            if (bytes != null && bytes.length > 0) {
                sb.append("\nBody:\n")
                        .append(new String(bytes, StandardCharsets.UTF_8))
                        .append("\n");
            }
        } catch (Exception e) {
            sb.append("\n<Failed to read response body: ")
                    .append(e.getMessage())
                    .append(">\n");
        }

        return sb.toString();
    }
}
