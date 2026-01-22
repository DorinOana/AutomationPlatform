package io.dorin.automationplatform.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Log4j2ConfigSmokeTest
 * <p>
 * Purpose:
 * --------
 * Verifies that Log4j2 loads an explicit configuration file (log4j2-test.xml)
 * from the classpath.
 * <p>
 * What this test validates:
 * -------------------------
 * - Log4j2 is the active logging implementation
 * - LoggerContext is correctly initialized
 * - A configuration file is actually loaded
 * - The configuration comes from "log4j2-test.xml" and not from a default fallback
 * <p>
 * Why this test exists:
 * ---------------------
 * If Log4j2 cannot find log4j2-test.xml, it silently falls back to a default
 * configuration. This can cause:
 * - missing logs
 * - inconsistent logging between local and CI
 * - hard-to-debug runtime behavior
 * <p>
 * This test ensures the platform never runs with an unintended default
 * logging configuration.
 * <p>
 * Tagging:
 * --------
 * Tagged as "smoke" so it runs early in CI and release validation flows.
 */
@Tag("smoke")
class Log4j2ConfigSmokeTest {

    @Test
    void log4j2_configuration_is_loaded_from_classpath() {
        // Retrieve the active Log4j2 context (do not force reconfiguration).
        LoggerContext context = (LoggerContext) LogManager.getContext(false);

        assertNotNull(context, "LoggerContext should not be null");

        Configuration config = context.getConfiguration();
        assertNotNull(config, "Log4j2 Configuration should not be null");

        // This checks that custom config was loaded (not a random fallback),
        // because the appender is defined in log4j2-test.xml.
        assertNotNull(
                config.getAppender("Console"),
                "Expected Log4j2 config to define a Console appender (log4j2-test.xml not loaded?)");

        assertTrue(
                config.toString().contains("log4j2-test.xml"),
                "Expected Log4j2 to load configuration from log4j2-test.xml, but was: " + config);
    }
}
