package com.patina.codebloom.utilities.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;

@ControllerAdvice
public class ControllerExceptionHandler {
    private final Reporter errorReporter;
    private final Env env;

    public ControllerExceptionHandler(final Reporter errorReporter, final Env env) {
        this.errorReporter = errorReporter;
        this.env = env;
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiResponder<?>> handleResponseStatusException(final ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(ApiResponder.failure(ex.getReason()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponder<?>> handleThrowable(final Throwable rx) {
        rx.printStackTrace();

        if (env.isProd() && ExcludedExceptions.isValid(rx)) {
            errorReporter.error(Report.builder()
                            .environments(env.getActiveProfiles())
                            .location(Location.BACKEND)
                            .data(Reporter.throwableToString(rx))
                            .build());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponder.failure(rx.getMessage()));
    }
}
