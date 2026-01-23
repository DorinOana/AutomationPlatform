package io.dorin.automationplatform.core.allure;

import io.qameta.allure.Allure;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * AllureAttachments
 *
 * Purpose:
 * -------
 * Minimal helper utilities for attaching diagnostic artifacts to Allure reports.
 *
 * Why this exists:
 * ---------------
 * - We want a small, reusable API for attaching common payloads (text / JSON / bytes)
 * - Avoid duplicating attachment boilerplate across modules (core / api / ui)
 * - Keep attachments consistent (mime types, extensions)
 *
 * Notes:
 * ------
 * - This class is intentionally small and dependency-light
 * - Do NOT add domain-specific concepts here (keep it generic)
 */
public final class AllureAttachments {

    private AllureAttachments() {
        // utility class
    }

    /**
     * Attach plain text content to Allure.
     */
    public static void text(String name, String content) {
        if (content == null) {
            content = "null";
        }
        Allure.addAttachment(
                name,
                "text/plain",
                new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)),
                ".txt"
        );
    }

    /**
     * Attach JSON content to Allure.
     *
     * Note:
     * - This does NOT validate JSON. Caller is responsible for providing JSON-formatted string.
     */
    public static void json(String name, String json) {
        if (json == null) {
            json = "null";
        }
        Allure.addAttachment(
                name,
                "application/json",
                new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)),
                ".json"
        );
    }

    /**
     * Attach arbitrary bytes to Allure with explicit mime type and extension.
     *
     * Example:
     * - bytes("screenshot", "image/png", pngBytes, ".png")
     */
    public static void bytes(String name, String mimeType, byte[] data, String extensionWithDot) {
        if (mimeType == null || mimeType.isBlank()) {
            mimeType = "application/octet-stream";
        }
        if (extensionWithDot == null || extensionWithDot.isBlank()) {
            extensionWithDot = ".bin";
        }
        if (data == null) {
            data = new byte[0];
        }

        Allure.addAttachment(
                name,
                mimeType,
                new ByteArrayInputStream(data),
                extensionWithDot
        );
    }
}
