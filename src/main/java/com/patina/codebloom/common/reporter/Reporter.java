package com.patina.codebloom.common.reporter;

import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;
import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import org.springframework.context.annotation.Primary;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Can either report an error or log data that can only be viewed in Discord.
 */
@Component
@Primary
public class Reporter {

    private final JDAClient jdaClient;

    public Reporter(final JDAClient jdaClient) {
        this.jdaClient = jdaClient;
    }

    /**
     * Convert the stacktrace of a {@linkplain Throwable} into a string.
     */
    public static String throwableToString(final Throwable throwable) {
        try (
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)
        ) {
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
    @Async
    public void error(final Report report) {
        String description = String.format(
            """
            An error occurred in Codebloom.

            Active environment(s): %s
            Current Time: %s
            Location: %s

            Check attachment for stack trace.""",
            report.getEnvironments(),
            StandardizedOffsetDateTime.now().toString(),
            report.getLocation().getResolvedName()
        );

        jdaClient.sendEmbedWithImage(
            EmbeddedMessageOptions.builder()
                .guildId(
                    jdaClient.getJdaErrorReportingProperties().getGuildId()
                )
                .channelId(
                    jdaClient.getJdaErrorReportingProperties().getChannelId()
                )
                .title("Something went wrong!")
                .description(description)
                .color(Color.RED)
                .fileName("stacktrace.txt")
                .fileBytes(report.getData().getBytes())
                .build()
        );
    }

    /**
     * Report a log.
     */
    @Async
    public void log(final Report report) {
        String description = String.format(
            """
            Log request has been triggered.

            Active environment(s): %s
            Current Time: %s
            Location: %s

            Check attachment for data.""",
            report.getEnvironments(),
            StandardizedLocalDateTime.now().toString(),
            report.getLocation().getResolvedName()
        );

        jdaClient.sendEmbedWithImage(
            EmbeddedMessageOptions.builder()
                .guildId(jdaClient.getJdaLogReportingProperties().getGuildId())
                .channelId(
                    jdaClient.getJdaLogReportingProperties().getChannelId()
                )
                .title("Log")
                .description(description)
                .color(Color.BLUE)
                .fileName("data.txt")
                .fileBytes(report.getData().getBytes())
                .build()
        );
    }
}
