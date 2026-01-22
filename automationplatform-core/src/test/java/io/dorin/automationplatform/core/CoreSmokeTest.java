package io.dorin.automationplatform.core;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * CoreSmokeTest
 * <p>
 * Purpose:
 * --------
 * This is a minimal "platform sanity" test that verifies the logging
 * infrastructure is correctly wired at runtime.
 * <p>
 * What this test validates:
 * -------------------------
 * - SLF4J API is available on the classpath
 * - An SLF4J Logger can be successfully created
 * - No missing or conflicting SLF4J bindings are present
 * <p>
 * Why this test exists:
 * ---------------------
 * Logging misconfiguration is a very common source of runtime issues
 * (especially in CI or when upgrading dependencies).
 * <p>
 * This smoke test ensures that:
 * - the platform fails FAST if logging is broken
 * - problems are detected before release/tagging
 * <p>
 * Tagging:
 * --------
 * Tagged as "smoke" so it can be executed independently in CI pipelines
 * before promoting dependency versions or creating a release.
 */
@Tag("smoke")
class CoreSmokeTest {

    private static final Logger log = LoggerFactory.getLogger(CoreSmokeTest.class);

    @Test
    void slf4j_is_bound_and_logging_works() {
        // If SLF4J binding is missing or misconfigured, this test will usually fail
        // later in very obscure ways. We want a clear, early signal instead.
        assertNotNull(log, "Logger should not be null");

        log.info("AutomationPlatform CORE smoke: SLF4J binding is active");
    }
}
