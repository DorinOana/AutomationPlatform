package io.dorin.automationplatform.core;

import io.dorin.automationplatform.core.allure.AllureAttachments;
import io.dorin.automationplatform.core.allure.AllureLinks;
import io.qameta.allure.junit5.AllureJunit5;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * AllureSmokeTest
 *
 * Purpose:
 * -------
 * Minimal platform sanity test that verifies Allure is wired correctly:
 * - JUnit 5 extension is active
 * - allure.results.directory is set by Surefire
 * - result files are generated in target/allure-results
 * - attachments are written
 *
 * Why this test exists:
 * ---------------------
 * Reporting is a core platform feature and must not silently break
 * when upgrading dependencies or plugins.
 *
 * Tagging:
 * --------
 * Tagged as "smoke" so it can be executed in CI pipelines with:
 * - mvn test -Dgroups=smoke
 */
@Tag("smoke")
//@ExtendWith(AllureJunit5.class)
class AllureSmokeTest {

    @Test
    void should_generate_allure_results_and_attachments() {
        AllureAttachments.text("smoke-attachment", "Allure is wired ✅");
        AllureAttachments.json("smoke-json", "{\"status\":\"ok\"}");

        // Link helpers smoke coverage (should not throw; should be serialized into results)
        AllureLinks.link("spec", "https://example.com/spec");
        AllureLinks.issue("JIRA-123", "https://example.com/jira/JIRA-123");
        AllureLinks.tms("TC-456", "https://example.com/tms/TC-456");

        // The actual filesystem verification is performed in @AfterAll
        assertTrue(true);
    }

    @AfterAll
    static void verify_allure_results_were_produced() throws IOException {
        String resultsDirProp = System.getProperty("allure.results.directory");
        assertNotNull(resultsDirProp, "allure.results.directory should be set by surefire config");

        Path resultsDir = Paths.get(resultsDirProp);
        assertTrue(Files.exists(resultsDir), "Allure results dir should exist: " + resultsDir);
        assertTrue(Files.isDirectory(resultsDir), "Allure results path should be a directory: " + resultsDir);

        awaitFiles(resultsDir, Duration.ofSeconds(2));

        long resultJsonCount;
        try (var files = Files.list(resultsDir)) {
            resultJsonCount = files
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.endsWith("-result.json"))
                    .count();
        }
        assertTrue(resultJsonCount > 0, "Expected at least one *-result.json in " + resultsDir);

        long attachmentCount;
        try (var files = Files.list(resultsDir)) {
            attachmentCount = files
                    .map(p -> p.getFileName().toString())
                    .filter(name -> name.contains("-attachment"))
                    .count();
        }
        assertTrue(attachmentCount > 0, "Expected at least one attachment file in " + resultsDir);
    }

    private static void awaitFiles(Path dir, Duration timeout) throws IOException {
        long deadline = System.nanoTime() + timeout.toNanos();

        while (System.nanoTime() < deadline) {
            try (var files = Files.list(dir)) {
                if (files.findAny().isPresent()) {
                    return;
                }
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }
}
