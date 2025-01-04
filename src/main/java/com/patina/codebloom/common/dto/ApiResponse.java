package com.patina.codebloom.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.data = success ? data : null;
        this.message = message;
    };

    /**
     * This should only be used when success is false.
     */
    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.data = null;
        this.message = message;
    };

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public void setSuccess(boolean success) {
        this.success = success;
        if (!success) {
            this.data = null;
        }
    }

    public void setData(T data) {
        if (success) {
            this.data = data;
        }
    }

    public void setMessage(String message) {
        this.message = message;
    }
}