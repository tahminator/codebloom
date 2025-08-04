package com.patina.codebloom.common.reporter;

import java.awt.Color;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;

@Component
public class ErrorReporter {
    private final JDAClient jdaClient;

    public ErrorReporter(final JDAClient jdaClient) {
        this.jdaClient = jdaClient;
    }

    @Async
    public void report(final Report report) {
        String description = String.format("""
                        An error occurred in Codebloom.

                        Active environment(s): %s
                        Current Time: %s
                        Location: %s

                        Check attachment for stack trace.""",
                        report.getEnvironments(),
                        StandardizedLocalDateTime.now().toString(),
                        report.getLocation().getResolvedName());

        jdaClient.sendEmbedWithImage(
                        EmbeddedMessageOptions.builder()
                                        .guildId(jdaClient.getJdaReportingProperties().getGuildId())
                                        .channelId(jdaClient.getJdaReportingProperties().getChannelId())
                                        .title("Something went wrong!")
                                        .description(description)
                                        .color(Color.RED)
                                        .fileName("stacktrace.txt")
                                        .fileBytes(report.getStackTrace())
                                        .build());
    }

}
