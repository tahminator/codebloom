package com.patina.codebloom.scheduled.auth;

import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.LoadState;
import com.patina.codebloom.common.db.models.auth.Auth;
import com.patina.codebloom.common.db.repos.auth.AuthRepository;
import com.patina.codebloom.common.email.Email;
import com.patina.codebloom.common.email.Message;
import com.patina.codebloom.common.email.client.github.GithubOAuthEmail;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.jedis.JedisClient;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LeetcodeAuthStealer {
    private static final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();

    private volatile String cookie;
    private volatile String csrf;

    /**
     * So we don't report that a csrf token is missing more than once per machine
     * lifecycle.
     */
    private boolean reported = false;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.password}")
    private String githubPassword;

    private final JedisClient jedisClient;
    private final AuthRepository authRepository;
    private final Email email;
    private final Reporter reporter;
    private final Env env;

    private static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9";

    public LeetcodeAuthStealer(final JedisClient jedisClient,
                    final AuthRepository authRepository,
                    final GithubOAuthEmail email,
                    final Reporter reporter,
                    final Env env) {
        this.jedisClient = jedisClient;
        this.authRepository = authRepository;
        this.email = email;
        this.reporter = reporter;
        this.env = env;
    }

    /**
     * <b>DO NOT RETURN THE TOKEN IN ANY API ENDPOINT.</b> <div /> This function
     * utilizes Playwright in order to get an authentication key from Leetcode. That
     * code is stored in the database and can then be used to run authenticated
     * queries such as used to retrieve code from our user submissions.
     */
    @Scheduled(initialDelay = 0, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void stealAuthCookie() {
        LOCK.writeLock().lock();
        try {
            Auth mostRecentAuth = authRepository.getMostRecentAuth();

            // The auth token should be refreshed every day.
            if (mostRecentAuth != null && mostRecentAuth.getCreatedAt().isAfter(StandardizedOffsetDateTime.now().minus(4, ChronoUnit.HOURS))) {
                log.info("Auth token already exists, using token from database.");
                cookie = mostRecentAuth.getToken();
                csrf = mostRecentAuth.getCsrf();
                if (env.isCi()) {
                    log.info("in ci, stealing token and putting it in cache for 1 day");
                    jedisClient.setAuth(cookie, 4, ChronoUnit.HOURS); // 4 hours.
                }
                return;
            }

            if (env.isCi()) {
                log.info("in ci env, checking redis client...");
                Optional<String> authToken = jedisClient.getAuth();

                log.info("auth token in redis = {}", authToken.isPresent());

                if (authToken.isPresent()) {
                    log.info("auth token found in redis client");
                    cookie = authToken.get();
                    csrf = null; // don't care in ci.
                    return;
                }

                log.info("auth token not found in redis client");
            }
        } finally {
            LOCK.writeLock().unlock();
        }

        log.info("Auth token is missing/expired. Attempting to receive token...");

        stealCookieImpl();
    }

    /**
     * There are some cases where leetcode.com may not respect the token anymore. If
     * that is the case, it is best to try to steal a new cookie and replace the
     * current one.
     *
     * You may await the `CompletableFuture` and receive the brand new token, or
     * call-and-forget.
     */
    @Async
    public CompletableFuture<Optional<String>> reloadCookie() {
        return CompletableFuture.completedFuture(Optional.ofNullable(stealCookieImpl()));
    }

    public String getCookie() {
        LOCK.readLock().lock();
        try {
            return cookie;
        } finally {
            LOCK.readLock().unlock();
        }
    }

    /**
     * It's fine if this is null for some requests; it isn't a requirement to fetch
     * data from the GraphQL layer of leetcode.com
     */
    public String getCsrf() {
        if (csrf == null && !reported) {
            reported = true;
            reporter.log(Report.builder()
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .data("CSRF token is missing inside of LeetcodeAuthStealer. This may be something to look into.")
                            .build());
        }

        return csrf;
    }

    private String stealCookieImpl() {
        LOCK.writeLock().lock();
        try (Playwright playwright = Playwright.create();
                        Browser browser = playwright.firefox().launch(new BrowserType.LaunchOptions().setHeadless(true).setTimeout(40000));
                        BrowserContext context = browser.newContext(new NewContextOptions()
                                        .setUserAgent(USER_AGENT)
                                        .setStorageState(null))) {
            context.clearCookies();

            log.info("Loaded browser context");

            Page page = context.newPage();

            page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");

            log.info("Navigated to leetcode.com login");

            page.waitForLoadState(LoadState.NETWORKIDLE);

            page.fill("#login_field", githubUsername);
            page.fill("#password", githubPassword);

            log.info("Filled in credentials, clicking login...");

            page.click("input[name=\"commit\"]");

            if (page.isVisible("#device-verification-prompt") || page.isVisible("#session-otp-input-description")) {
                log.info("2FA Required");
                List<Message> messages;

                page.waitForTimeout(10000);

                try {
                    messages = email.getPastMessages();
                } catch (Exception e) {
                    log.info("Failed to retrieve past messages");
                    throw new RuntimeException("Failed to retrieve past messages", e);
                }

                Message target = null;
                for (Message m : messages) {
                    // Don't break so we can get the newest available code.
                    if (m.getSubject().equals("[GitHub] Please verify your device")) {
                        log.info("Found verification email");
                        target = m;
                    }
                }

                if (target == null) {
                    throw new RuntimeException("Something went wrong when parsing the inbox. Manual intervention required");
                }

                String code = CodeExtractor.extractCode(target.getMessage());

                log.info("Found code in email, will fill now...");

                if (code == null) {
                    throw new RuntimeException("Code was not found in the email. Manual intervention required.");
                }

                page.fill("input[name='otp']", code);

                log.info("Page filled!");

            }

            if (page.isVisible("button[name=\"authorize\"]")) {
                List<ElementHandle> buttons = page.querySelectorAll("button[name=\"authorize\"]");
                if (buttons.size() > 1) {
                    // Click the second button (which is the actual authorize button)
                    buttons.get(1).click();
                }
                log.info("Authorization button clicked");
            }

            page.waitForURL("https://leetcode.com/");

            log.info("Back to leetcode.com!");

            // if (page.isVisible("h1:has-text(\"Login Cancelled\")")) {
            // page.navigate("https://leetcode.com/accounts/github/login/?next=%2F");
            // page.waitForTimeout(100000);
            // }

            if (page.url().equals("https://leetcode.com/")) {
                Map<String, String> cookieMap = page.context().cookies().stream()
                                .filter(cookie -> cookie.name.equals("LEETCODE_SESSION") || cookie.name.equals("csrftoken"))
                                .collect(Collectors.toMap(cookie -> cookie.name, cookie -> cookie.value));

                String sessionToken = cookieMap.get("LEETCODE_SESSION");
                String csrf = cookieMap.get("csrftoken");
                if (sessionToken != null) {
                    log.info("Cookie found!");
                    authRepository.createAuth(Auth
                                    .builder()
                                    .token(sessionToken)
                                    .csrf(cookieMap.get("csrftoken"))
                                    .build());
                    if (env.isCi()) {
                        log.info("in ci, stored in redis as well");
                        jedisClient.setAuth(sessionToken, 4, ChronoUnit.HOURS); // 4 hours.
                    }
                    this.cookie = sessionToken;
                    this.csrf = csrf;
                    return sessionToken;
                }
            } else {
                log.info("Should be authenticated but not authenticated.");
            }
        } finally {
            LOCK.writeLock().unlock();
        }

        return null;
    }

}
