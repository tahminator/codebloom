package com.patina.codebloom.common.dto.autogen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.patina.codebloom.common.dto.autogen.overrides.QuestionOverride;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Please do not use this unless you know what you are doing.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UnsafeSubmissionSuccessResponse {
    @Schema(defaultValue = "true")
    private boolean success;
    private String message;
    private QuestionOverride data;

    public QuestionOverride getData() {
        return data;
    }

    public void setData(final QuestionOverride data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
