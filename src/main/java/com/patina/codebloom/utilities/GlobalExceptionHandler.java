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
import com.patina.codebloom.common.reporter.ErrorReporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.jda.client.options.EmbeddedMessageOptions;

@ControllerAdvice
public class GlobalExceptionHandler {
    private final ErrorReporter errorReporter;
    private final Env env;

    public GlobalExceptionHandler(final ErrorReporter errorReporter, final Env env) {
        this.errorReporter = errorReporter;
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
            errorReporter.report(Report.builder()
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .stackTrace(getStackTraceAsBytes(rx))
                            .build());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponder.failure(rx.getMessage()));
    }
}
