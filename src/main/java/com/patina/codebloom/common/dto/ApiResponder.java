package com.patina.codebloom.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Use the generic methods for failure and success as they are simpler to use.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponder<T> {
    private boolean success;
    private T data;
    private String message;

    private ApiResponder(final boolean success, final String message, final T data) {
        this.success = success;
        this.data = data;
        this.message = message;
    };

    private ApiResponder(final boolean success, final String message) {
        this.success = success;
        this.data = null;
        this.message = message;
    };

    public static <T> ApiResponder<T> success(final String message, final T data) {
        return new ApiResponder<>(true, message, data);
    }

    public static <T> ApiResponder<T> failure(final String message) {
        return new ApiResponder<>(false, message, null);
    }

    public static <T> ApiResponder<T> custom(final boolean success, final String message, final T data) {
        return new ApiResponder<>(success, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }
}
