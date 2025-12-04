package com.patina.codebloom.utilities.exception;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponder<?>> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex) {
        var errors =
                ex.getFieldErrors().stream().map(e -> e.getDefaultMessage()).toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponder.failure(String.join(", ", errors)));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponder<?>> handleMethodArgumentNotValid(final ValidationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponder.failure(ex.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ApiResponder<?>> handleThrowable(final Throwable rx) {
        rx.printStackTrace();

        if (ExcludedExceptions.isValid(rx)) {
            errorReporter.error(Report.builder()
                    .environments(env.getActiveProfiles())
                    .location(Location.BACKEND)
                    .data(Reporter.throwableToString(rx))
                    .build());
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponder.failure(rx.getMessage()));
    }
}
