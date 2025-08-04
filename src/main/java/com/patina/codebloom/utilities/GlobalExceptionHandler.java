package com.patina.codebloom.utilities;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.jda.client.JDAClient;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final JDAClient jdaClient;
    private final Env env;

    public GlobalExceptionHandler(final JDAClient jdaClient, final Env env) {
        this.jdaClient = jdaClient;
        this.env = env;
    }

    private byte[] getStackTraceAsBytes(final Throwable throwable) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8)) {
            throwable.printStackTrace(ps);
            ps.flush();
            return baos.toByteArray();
        } catch (Exception e) {
            return ("Failed to capture stack trace: " + e.getMessage()).getBytes(StandardCharsets.UTF_8);
        }
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponder<?>> handleResponseStatusException(final ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponder.failure(ex.getReason()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponder<?>> handleRuntimeException(final RuntimeException rx) {
        rx.printStackTrace();

        if (env.isProd()) {
            String description = String.format("""
                            An error occurred in Codebloom.

                            Active profile(s): %s
                            Current Time: %s

                            Check attachment for stack trace.""",
                            env.getActiveProfiles(),
                            StandardizedLocalDateTime.now().toString());
            jdaClient.sendEmbedWithImage(
                            EmbeddedMessageOptions.builder()
                                            .guildId(jdaClient.getJdaReportingProperties().getGuildId())
                                            .channelId(jdaClient.getJdaReportingProperties().getChannelId())
                                            .title("Something went wrong!")
                                            .description(description)
                                            .color(Color.RED)
                                            .fileName("stacktrace.txt")
                                            .fileBytes(getStackTraceAsBytes(rx))
                                            .build());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponder.failure(rx.getMessage()));
    }
}
