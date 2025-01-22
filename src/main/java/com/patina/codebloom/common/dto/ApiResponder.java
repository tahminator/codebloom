package com.patina.codebloom.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Use the generic methods for failure and success as they are simpler to use.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponder<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResponder(boolean success, String message, T data) {
        this.success = success;
        this.data = success ? data : null;
        this.message = message;
    };

    public ApiResponder(boolean success, String message) {
        this.success = success;
        this.data = null;
        this.message = message;
    };

    public static <T> ApiResponder<T> success(String message, T data) {
        return new ApiResponder<>(true, message, data);
    }

    public static <T> ApiResponder<T> failure(String message) {
        return new ApiResponder<>(false, message, null);
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