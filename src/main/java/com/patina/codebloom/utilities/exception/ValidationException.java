package com.patina.codebloom.utilities.exception;

/** Unchecked (runtime) exception used when manually writing validation logic. */
public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }
}
