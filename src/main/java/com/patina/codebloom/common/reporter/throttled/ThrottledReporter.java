package com.patina.codebloom.common.reporter.throttled;

import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.jda.client.JDAClient;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Attaches a rate limiter over {@link Reporter} to avoid too many user submission reports */
@Component
public class ThrottledReporter extends Reporter {

    private final Bucket rateLimiter;
    private final Map<String, ReportCounter> counters = new ConcurrentHashMap<>();
    private static final int OCCURENCE_THRESHOLD = 3;
    private static final long TIME_LIMIT_MINUTES = 30;

    private Bucket initializeBucket() {
        var bandwidth = Bandwidth.builder()
                .capacity(1L)
                .refillIntervally(1L, Duration.ofMinutes(2))
                .build();

        return Bucket.builder().addLimit(bandwidth).build();
    }

    private boolean checkToken() {
        return rateLimiter.tryConsume(1);
    }

    public ThrottledReporter(final JDAClient jdaClient) {
        super(jdaClient);
        this.rateLimiter = initializeBucket();
    }

    protected long now() {
        return System.currentTimeMillis();
    }

    /** Convert the stacktrace of a {@linkplain Throwable} into a string. */
    public static String throwableToString(final Throwable throwable) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            throwable.printStackTrace(ps);
            ps.flush();
            return baos.toString();
        } catch (Exception e) {
            return "Failed to capture stack trace: " + e.getMessage();
        }
    }

    /** Report an error. */
    @Override
    @Async
    public void error(@NonNull String key, final Report report) {
        if (!checkToken()) {
            return;
        }
        if (shouldReport(key)) {
            super.error(key, report);
        }
    }

    /** Report a log. */
    @Override
    @Async
    public void log(@NonNull String key, final Report report) {
        if (!checkToken()) {
            return;
        }
        if (shouldReport(key)) {
            super.log(key, report);
        }
    }
    /**
     * Determine if a report should be sent. If threshold is reached within the time limit, the error will be reported.
     *
     * @param key a non-null identifer to group report types
     * @return {@code true} if the report should be sent, {@code false} otherwise
     */
    public boolean shouldReport(@NonNull String key) {
        long now = now();

        ReportCounter counter = counters.compute(key, (k, v) -> {
            if (v == null || now > v.getExpireTime()) {
                return new ReportCounter(
                        new AtomicInteger(1),
                        now + Duration.ofMinutes(TIME_LIMIT_MINUTES).toMillis());
            }
            v.getCount().incrementAndGet();
            return v;
        });

        if (counter.getCount().get() >= OCCURENCE_THRESHOLD) {
            return counters.remove(key, counter);
        }
        return false;
    }

    /** Clean up anything that hadn't been reported every 30 minutes. */
    @Scheduled(fixedRate = TIME_LIMIT_MINUTES, timeUnit = TimeUnit.MINUTES)
    public void cleanUp() {
        long now = now();
        counters.entrySet().removeIf(entry -> now > entry.getValue().getExpireTime());
    }
}
