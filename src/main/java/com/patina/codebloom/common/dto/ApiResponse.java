package com.patina.codebloom.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Use the generic methods for failure and success as they are simpler to use.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    private ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.data = success ? data : null;
        this.message = message;
    };

    private ApiResponse(boolean success, String message) {
        this.success = success;
        this.data = null;
        this.message = message;
    };

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    public static <T> ApiResponse<T> failure(String message) {
        return new ApiResponse<>(false, message, null);
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