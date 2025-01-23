package com.patina.codebloom.common.dto.autogen;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.patina.codebloom.common.dto.autogen.overrides.QuestionOverride;

import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_SUBMISSION_SUCCESS_RESPONSE {
    @Schema(defaultValue = "true")
    private boolean success;
    private String message;
    private QuestionOverride data;

    public QuestionOverride getData() {
        return data;
    }

    public void setData(QuestionOverride data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}