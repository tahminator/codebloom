package org.patinanetwork.codebloom.common.reporter.throttled;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import lombok.NonNull;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.reporter.report.Report;
import org.patinanetwork.codebloom.jda.client.JDAClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/** Attaches a rate limiter over {@link Reporter} to avoid too many user submission reports */
@Component
public class ThrottledReporter extends Reporter {

    private final Bucket rateLimiter;

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
        super.error(key, report);
    }

    /** Report a log. */
    @Override
    @Async
    public void log(@NonNull String key, final Report report) {
        if (!checkToken()) {
            return;
        }
        super.log(key, report);
    }
}
