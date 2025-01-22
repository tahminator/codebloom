package com.patina.codebloom.common.dto.autogen;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EMPTY_SUCCESS_RESPONSE {
    @Schema(defaultValue = "true")
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
