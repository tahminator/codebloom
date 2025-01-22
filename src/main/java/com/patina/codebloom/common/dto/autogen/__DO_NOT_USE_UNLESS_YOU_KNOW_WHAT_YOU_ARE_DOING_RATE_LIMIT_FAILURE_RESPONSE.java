package com.patina.codebloom.common.dto.autogen;

import io.swagger.v3.oas.annotations.media.Schema;

public class __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_RATE_LIMIT_FAILURE_RESPONSE {
    @Schema(defaultValue = "false")
    private boolean success;
    @Schema(defaultValue = "You have already scanned your submissions recently. You may try again in 28 seconds.")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
