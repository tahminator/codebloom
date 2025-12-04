package com.patina.codebloom.common.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;

/** Use the generic methods for failure and success as they are simpler to use. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponder<T> {

    private boolean success;
    private T payload;
    private String message;

    @JsonCreator
    private ApiResponder(final boolean success, final String message, final T payload) {
        this.success = success;
        this.payload = payload;
        this.message = message;
    }

    private ApiResponder(final boolean success, final String message) {
        this.success = success;
        this.payload = null;
        this.message = message;
    }

    public static <T> ApiResponder<T> success(final String message, final T payload) {
        return new ApiResponder<>(true, message, payload);
    }

    public static <T> ApiResponder<T> failure(final String message) {
        return new ApiResponder<>(false, message, null);
    }

    public static <T> ApiResponder<T> custom(final boolean success, final String message, final T payload) {
        return new ApiResponder<>(success, message, payload);
    }

    public boolean isSuccess() {
        return success;
    }

    public T getPayload() {
        return payload;
    }

    public String getMessage() {
        return message;
    }
}
