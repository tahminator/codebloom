package com.patina.codebloom.scheduled.auth;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.Cookie;
import com.microsoft.playwright.options.LoadState;

@Component
/**
 * TODO - Handle 2FA Case
 * TODO - Handle account swapping (improve success probability if script fails).
 * TODO - Create database table to store the key as well as store the date
 * retrieved to avoid running the task until ~12 days pass (LEETCODE_SESSION
 * cookie returns with an expiration date of 14 days).
 */
public class LeetcodeAuthStealer {
    private static String cookie;

    /**
     * <b>DO NOT RETURN THE TOKEN IN ANY API ENDPOINT.</b>
     * <div />
     * This function utilizes Playwright in order to get an authentication
     * key from
     * Leetcode. That code is stored in the database and can then be used to run
     * authenticated queries such as used to retrieve code from
     * our user submissions.
     */
    @Scheduled(initialDelay = 0, fixedDelay = 1000000)
    public void stealAuthCookie() {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox()
                    .launch(new BrowserType.LaunchOptions().setHeadless(false).setTimeout(5000));
            BrowserContext context = browser.newContext(new NewContextOptions()
                    .setUserAgent(
                            "Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko)  Chrome/48.0.2090.359 Mobile Safari/601.9")
                    .setStorageState(null));
            context.clearCookies();

            Page page = context.newPage();

            page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");

            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.fill("#login_field", "REDACTED");
            page.fill("#password", "REDACTED");

            page.click("input[name=\"commit\"]");

            // TODO - Handle this case.
            if (page.isVisible("#device-verification-prompt") || page.isVisible("#session-otp-input-description")) {
                page.waitForTimeout(200000);
            }

            if (page.isVisible("button[name=\"authorize\"]")) {
                List<ElementHandle> buttons = page.querySelectorAll("button[name=\"authorize\"]");
                if (buttons.size() > 1) {
                    // Click the second button (which is the actual authorize button)
                    buttons.get(1).click();
                }
            }

            page.waitForURL("https://leetcode.com/");

            // if (page.isVisible("h1:has-text(\"Login Cancelled\")")) {
            // page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");
            // page.waitForTimeout(100000);
            // }

            if (page.url().equals("https://leetcode.com/")) {
                for (Cookie cookie : page.context().cookies()) {
                    if (cookie.name.equals("LEETCODE_SESSION")) {
                        System.out.println(cookie.name + " " + cookie.value);
                    }
                }
            } else {
                System.err.println("Should be authenticated but not authenticated.");
            }

            context.close();
            browser.close();
        }
    }

    public static String getCookie() {
        return cookie;
    }

    public static void setCookie(String cookie) {
        LeetcodeAuthStealer.cookie = cookie;
    }
}
