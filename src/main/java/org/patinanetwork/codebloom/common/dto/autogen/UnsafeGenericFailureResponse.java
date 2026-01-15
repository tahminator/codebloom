package org.patinanetwork.codebloom.common.dto.autogen;

import io.swagger.v3.oas.annotations.media.Schema;

/** Please do not use this unless you know what you are doing. */
public class UnsafeGenericFailureResponse {

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
