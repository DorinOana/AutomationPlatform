package io.dorin.automationplatform.core.allure;

import io.qameta.allure.Allure;

/**
 * AllureLinks
 *
 * Purpose:
 * -------
 * Small helper utilities for adding common link types to Allure reports:
 * - generic links (URL + label)
 * - issue links (defect tracking)
 * - TMS links (test management)
 *
 * Why this exists:
 * ---------------
 * - Keep a consistent approach across modules (core / api / ui)
 * - Avoid repeating Allure link boilerplate in tests
 * - Make it easy to standardize link naming conventions later
 *
 * Notes:
 * ------
 * - This class does NOT enforce a specific tracker/TMS URL format.
 *   Consumers may build URLs externally (e.g., Jira, Azure DevOps) and pass them in.
 * - Keep it generic (no domain-specific rules here).
 */
public final class AllureLinks {

    private AllureLinks() {
        // utility class
    }

    /**
     * Adds a generic link to the current test/step.
     *
     * Example:
     *  AllureLinks.link("Spec", "https://confluence/.../page");
     */
    public static void link(String name, String url) {
        if (name == null || name.isBlank()) {
            name = "link";
        }
        if (url == null) {
            url = "";
        }
        Allure.link(name, url);
    }

    /**
     * Adds an "issue" link (bug/defect) to the current test/step.
     *
     * Example:
     *  AllureLinks.issue("JIRA-123", "https://jira/.../JIRA-123");
     */
    public static void issue(String issueId, String url) {
        if (issueId == null || issueId.isBlank()) {
            issueId = "issue";
        }
        if (url == null) {
            url = "";
        }
        Allure.issue(issueId, url);
    }

    /**
     * Adds a "TMS" link (Test Management System) to the current test/step.
     *
     * Example:
     *  AllureLinks.tms("TC-456", "https://testrail/.../TC-456");
     */
    public static void tms(String testCaseId, String url) {
        if (testCaseId == null || testCaseId.isBlank()) {
            testCaseId = "tms";
        }
        if (url == null) {
            url = "";
        }
        Allure.tms(testCaseId, url);
    }
}
