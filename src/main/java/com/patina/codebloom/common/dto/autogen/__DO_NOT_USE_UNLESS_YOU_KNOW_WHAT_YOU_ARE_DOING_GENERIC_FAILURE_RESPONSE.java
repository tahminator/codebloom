package com.patina.codebloom.common.dto.autogen;

import io.swagger.v3.oas.annotations.media.Schema;

public class __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_GENERIC_FAILURE_RESPONSE {
    @Schema(defaultValue = "false")
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
