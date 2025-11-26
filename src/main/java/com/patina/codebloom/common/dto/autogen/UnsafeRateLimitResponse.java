package com.patina.codebloom.common.dto.autogen;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Please do not use this unless you know what you are doing.
 */
public class UnsafeRateLimitResponse {

    @Schema(defaultValue = "false")
    private boolean success;

    @Schema(defaultValue = "28")
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
