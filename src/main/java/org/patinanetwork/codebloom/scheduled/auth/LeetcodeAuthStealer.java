package org.patinanetwork.codebloom.scheduled.auth;

import com.google.common.annotations.VisibleForTesting;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.db.models.auth.Auth;
import org.patinanetwork.codebloom.common.db.repos.auth.AuthRepository;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.jedis.JedisClient;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.reporter.report.Report;
import org.patinanetwork.codebloom.common.reporter.report.location.Location;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.patinanetwork.codebloom.playwright.PlaywrightClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LeetcodeAuthStealer {

    @VisibleForTesting
    static final String METRIC_NAME = "leetcode.client.execution";

    @VisibleForTesting
    // CHECKSTYLE:OFF
    final ReentrantReadWriteLock LOCK = new ReentrantReadWriteLock();
    // CHECKSTYLE:ON

    private volatile String cookie;
    private volatile String csrf;

    /** So we don't report that a csrf token is missing more than once per machine lifecycle. */
    private boolean reported = false;

    @Value("${github.username}")
    private String githubUsername;

    @Value("${github.password}")
    private String githubPassword;

    private final JedisClient jedisClient;
    private final AuthRepository authRepository;
    private final Reporter reporter;
    private final Env env;
    private final MeterRegistry meterRegistry;
    private final PlaywrightClient playwrightClient;

    public LeetcodeAuthStealer(
            final JedisClient jedisClient,
            final AuthRepository authRepository,
            final Reporter reporter,
            final Env env,
            MeterRegistry meterRegistry,
            PlaywrightClient playwrightClient) {
        this.jedisClient = jedisClient;
        this.authRepository = authRepository;
        this.reporter = reporter;
        this.env = env;
        this.meterRegistry = meterRegistry;
        this.playwrightClient = playwrightClient;
    }

    private Timer timer() {
        var stackFrame = StackWalker.getInstance()
                .walk(frames -> frames.skip(1).findFirst())
                .orElseThrow();

        String methodName = stackFrame.getMethodName();
        String className = stackFrame.getClassName();

        return meterRegistry.timer(METRIC_NAME, "class", className, "method", methodName);
    }

    /**
     * <b>DO NOT RETURN THE TOKEN IN ANY API ENDPOINT.</b> <div /> This function utilizes Playwright in order to get an
     * authentication key from Leetcode. That code is stored in the database and can then be used to run authenticated
     * queries such as used to retrieve code from our user submissions.
     */
    @Scheduled(initialDelay = 0, fixedDelay = 1, timeUnit = TimeUnit.HOURS)
    public void stealAuthCookie() {
        timer().record(() -> {
            LOCK.writeLock().lock();
            try {
                Auth mostRecentAuth = authRepository.getMostRecentAuth();

                // The auth token should be refreshed every day.
                if (mostRecentAuth != null
                        && mostRecentAuth
                                .getCreatedAt()
                                .isAfter(StandardizedOffsetDateTime.now().minus(4, ChronoUnit.HOURS))) {
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
                log.info("Auth token is missing/expired. Attempting to receive token...");

                stealCookieImpl();
            } finally {
                LOCK.writeLock().unlock();
            }
        });
    }

    /**
     * There are some cases where leetcode.com may not respect the token anymore. If that is the case, it is best to try
     * to steal a new cookie and replace the current one.
     *
     * <p>You may await the `CompletableFuture` and receive the brand new token, or call-and-forget.
     */
    @Async
    public CompletableFuture<Optional<String>> reloadCookie() {
        return timer().record(() -> CompletableFuture.completedFuture(Optional.ofNullable(stealCookieImpl())));
    }

    public String getCookie() {
        return timer().record(() -> {
            LOCK.readLock().lock();
            try {
                return cookie;
            } finally {
                LOCK.readLock().unlock();
            }
        });
    }

    /**
     * It's fine if this is null for some requests; it isn't a requirement to fetch data from the GraphQL layer of
     * leetcode.com
     */
    public String getCsrf() {
        return timer().record(() -> {
            if (csrf == null && !reported) {
                reported = true;
                reporter.log(
                        "getCsrf",
                        Report.builder()
                                .environments(env.getActiveProfiles())
                                .location(Location.BACKEND)
                                .data(
                                        "CSRF token is missing inside of LeetcodeAuthStealer. This may be something to look into.")
                                .build());
            }

            return csrf;
        });
    }

    String stealCookieImpl() {
        return timer().record(() -> {
            LOCK.writeLock().lock();
            try {
                Optional<Auth> auth = playwrightClient.getLeetcodeCookie(githubUsername, githubPassword);
                if (auth.isPresent()) {
                    var a = auth.get();
                    this.csrf = a.getCsrf();
                    this.cookie = a.getToken();
                    if (env.isCi()) {
                        log.info("in ci, stored in redis as well");
                        jedisClient.setAuth(a.getToken(), 4, ChronoUnit.HOURS); // 4 hours.
                    }
                    return cookie;
                }
            } finally {
                LOCK.writeLock().unlock();
            }
            return null;
        });
    }
}
