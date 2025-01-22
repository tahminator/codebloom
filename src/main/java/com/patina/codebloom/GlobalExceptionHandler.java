package com.patina.codebloom;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.common.dto.ApiResponder;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        if (ex.getStatusCode() == HttpStatus.UNAUTHORIZED) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponder.failure("You are not authorized"));
        }

        if (ex.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(ApiResponder.failure(ex.getReason()));
        }

        if (ex.getStatusCode() == HttpStatus.PRECONDITION_FAILED) {
            return ResponseEntity.status(HttpStatus.PRECONDITION_FAILED).body(ApiResponder.failure(ex.getReason()));
        }

        if (ex.getStatusCode() == HttpStatus.CONFLICT) {
            ApiResponder specialErrorResponse = new ApiResponder<int>();
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponder.failure(ex.getReason()));
        }

        return ResponseEntity
                .status(ex.getStatusCode())
                .body(ex.getReason());
    }
}