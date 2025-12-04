package com.patina.codebloom.common.dto.autogen;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

/** Please do not use this unless you know what you are doing. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnsafeEmptySuccessResponse {

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
