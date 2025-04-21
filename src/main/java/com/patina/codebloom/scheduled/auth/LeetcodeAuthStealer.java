package com.patina.codebloom.scheduled.auth;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
import com.patina.codebloom.common.db.models.auth.Auth;
import com.patina.codebloom.common.db.repos.auth.AuthRepository;
import com.patina.codebloom.common.email.Email;
import com.patina.codebloom.common.email.MessageLite;

@Component
public class LeetcodeAuthStealer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LeetcodeAuthStealer.class);
    private String cookie;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.password}")
    private String githubPassword;

    private final AuthRepository authRepository;
    private final Email email;

    public LeetcodeAuthStealer(final AuthRepository authRepository, final Email email) {
        this.authRepository = authRepository;
        this.email = email;
    }

    /**
     * <b>DO NOT RETURN THE TOKEN IN ANY API ENDPOINT.</b> <div /> This function
     * utilizes Playwright in order to get an authentication key from Leetcode. That
     * code is stored in the database and can then be used to run authenticated
     * queries such as used to retrieve code from our user submissions.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public synchronized void stealAuthCookie() {
        Auth mostRecentAuth = authRepository.getMostRecentAuth();

        // The auth token should be refreshed every day.
        if (mostRecentAuth != null && mostRecentAuth.getCreatedAt().isAfter(LocalDateTime.now().minus(4, ChronoUnit.DAYS))) {
            LOGGER.info("Auth token already exists, using token from database.");
            cookie = mostRecentAuth.getToken();
            return;
        }

        LOGGER.info("Auth token is missing/expired. Attempting to receive token...");

        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(40000));
            BrowserContext context = browser.newContext(new NewContextOptions()
                            .setUserAgent("Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9")
                            .setStorageState(null));
            context.clearCookies();

            LOGGER.info("Loaded browser context");

            Page page = context.newPage();

            page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");

            LOGGER.info("Navigated to leetcode.com login");

            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.fill("#login_field", githubUsername);
            page.fill("#password", githubPassword);

            LOGGER.info("Filled in credentials, clicking login...");

            page.click("input[name=\"commit\"]");

            if (page.isVisible("#device-verification-prompt") || page.isVisible("#session-otp-input-description")) {
                LOGGER.info("2FA Required");
                List<MessageLite> messages;

                page.waitForTimeout(10000);

                try {
                    messages = email.getPastMessages();
                } catch (Exception e) {
                    LOGGER.info("Failed to retrieve past messages");
                    throw new RuntimeException("Failed to retrieve past messages", e);
                }

                MessageLite target = null;
                for (MessageLite m : messages) {
                    // Don't break so we can get the newest available code.
                    if (m.getSubject().equals("[GitHub] Please verify your device")) {
                        LOGGER.info("Found verification email");
                        target = m;
                    }
                }

                if (target == null) {
                    throw new RuntimeException("Something went wrong when parsing the inbox. Manual intervention required");
                }

                String code = CodeExtractor.extractCode(target.getMessage());

                LOGGER.info("Found code in email, will fill now...");

                if (code == null) {
                    throw new RuntimeException("Code was not found in the email. Manual intervention required.");
                }

                page.fill("input[name='otp']", code);

                LOGGER.info("Page filled!");

            }

            if (page.isVisible("button[name=\"authorize\"]")) {
                List<ElementHandle> buttons = page.querySelectorAll("button[name=\"authorize\"]");
                if (buttons.size() > 1) {
                    // Click the second button (which is the actual authorize button)
                    buttons.get(1).click();
                }
                LOGGER.info("Authorization button clicked");
            }

            page.waitForURL("https://leetcode.com/");

            LOGGER.info("Back to leetcode.com!");

            // if (page.isVisible("h1:has-text(\"Login Cancelled\")")) {
            // page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");
            // page.waitForTimeout(100000);
            // }

            if (page.url().equals("https://leetcode.com/")) {
                for (Cookie cookie : page.context().cookies()) {
                    if (cookie.name.equals("LEETCODE_SESSION")) {
                        // System.out.println(cookie.name + " " + cookie.value);
                        try {
                            LOGGER.info("Cookie found!");
                            authRepository.createAuth(new Auth(cookie.value));
                            this.cookie = cookie.value;
                        } catch (Exception e) {
                            System.err.println(e);
                        }
                    }
                }
            } else {
                System.err.println("Should be authenticated but not authenticated.");
            }

            context.close();
            browser.close();
        }
    }

    public synchronized String getCookie() {
        if (cookie == null) {
            stealAuthCookie();
        }
        return cookie;
    }
}
