package io.dorin.automationplatform.core.allure;

import io.qameta.allure.Allure;

import java.util.concurrent.Callable;

/**
 * AllureSteps
 *
 * Purpose:
 * -------
 * Minimal helpers for structuring tests with explicit Allure steps.
 *
 * Why this exists:
 * ---------------
 * - Consistent step naming and formatting across modules (core / api / ui)
 * - Avoid repeating Allure.step(...) boilerplate
 *
 * Notes:
 * ------
 * - Keep this generic. No domain-specific logic here.
 */
public final class AllureSteps {

    private AllureSteps() {
        // utility class
    }

    public static void step(String name, Runnable action) {
        Allure.step(name, action::run);
    }

    public static <T> T step(String name, Callable<T> action) {
        return Allure.step(name, () -> {
            try {
                return action.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
