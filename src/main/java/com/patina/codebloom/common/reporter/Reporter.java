package com.patina.codebloom.common.reporter;

import com.google.common.annotations.VisibleForTesting;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.throttled.ReportCounter;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Can either report an error or log data that can only be viewed in Discord. */
@Component
@Primary
public class Reporter {

    private final JDAClient jdaClient;
    private final Map<String, ReportCounter> counters = new ConcurrentHashMap<>();
    private static final int OCCURENCE_THRESHOLD = 3;
    private static final long TIME_LIMIT_MINUTES = 30;

    public Reporter(final JDAClient jdaClient) {
        this.jdaClient = jdaClient;
    }

    @VisibleForTesting
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

    /**
     * Report an error.
     *
     * @param key a non-null identifer to group report types
     * @param report the detailed report object containing error metadata
     */
    @Async
    public void error(@NonNull String key, final Report report) {
        if (!shouldReport(key)) {
            return;
        }
        String description = String.format(
                """
            An error occurred in Codebloom.

            Active environment(s): %s
            Current Time: %s
            Location: %s

            Check attachment for stack trace.""",
                report.getEnvironments(),
                StandardizedOffsetDateTime.now().toString(),
                report.getLocation().getResolvedName());

        jdaClient.sendEmbedWithImage(EmbeddedMessageOptions.builder()
                .guildId(jdaClient.getJdaErrorReportingProperties().getGuildId())
                .channelId(jdaClient.getJdaErrorReportingProperties().getChannelId())
                .title("Something went wrong!")
                .description(description)
                .color(Color.RED)
                .fileName("stacktrace.txt")
                .fileBytes(report.getData().getBytes())
                .build());
    }

    /**
     * Report a log.
     *
     * @param key a non-null identifer to group report types
     * @param report the detailed report object containing error metadata
     */
    @Async
    public void log(@NonNull String key, final Report report) {
        if (!shouldReport(key)) {
            return;
        }
        String description = String.format(
                """
            Log request has been triggered.

            Active environment(s): %s
            Current Time: %s
            Location: %s

            Check attachment for data.""",
                report.getEnvironments(),
                StandardizedLocalDateTime.now().toString(),
                report.getLocation().getResolvedName());

        jdaClient.sendEmbedWithImage(EmbeddedMessageOptions.builder()
                .guildId(jdaClient.getJdaLogReportingProperties().getGuildId())
                .channelId(jdaClient.getJdaLogReportingProperties().getChannelId())
                .title("Log")
                .description(description)
                .color(Color.BLUE)
                .fileName("data.txt")
                .fileBytes(report.getData().getBytes())
                .build());
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
