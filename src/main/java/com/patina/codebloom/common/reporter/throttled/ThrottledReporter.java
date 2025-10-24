package com.patina.codebloom.common.reporter.throttled;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.jda.client.JDAClient;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

/**
 * Attaches a rate limiter over {@link Reporter} to avoid too many user
 * submission reports
 */
@Component
public class ThrottledReporter extends Reporter {
    private final Bucket rateLimiter;

    private Bucket initializeBucket() {
        var bandwidth = Bandwidth.builder()
                        .capacity(1L)
                        .refillIntervally(1L, Duration.ofMillis(120000))
                        .build();

        return Bucket.builder()
                        .addLimit(bandwidth)
                        .build();
    }

    public ThrottledReporter(JDAClient jdaClient) {
        super(jdaClient);
        this.rateLimiter = initializeBucket();
    }

    /**
     * Convert the stacktrace of a {@linkplain Throwable} into a string.
     */
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

    /**
     * Report an error.
     */
    @Override
    @Async
    public void error(final Report report) {
        if (!rateLimiter.tryConsume(1)) {
            return;
        }
        super.error(report);
    }

    /**
     * Report a log.
     */
    @Override
    @Async
    public void log(final Report report) {
        if (!rateLimiter.tryConsume(1)) {
            return;
        }
        super.log(report);
    }
}
