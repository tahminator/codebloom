package com.patina.codebloom.common.dto.autogen;

import java.util.ArrayList;

import com.patina.codebloom.website.leetcode.services.AcceptedSubmission;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Please do not use this unless you know what you are
 * doing.
 */
public class UnsafeSubmissionCheckSuccessResponse {
    @Schema(defaultValue = "true")
    private boolean success;
    @Schema(defaultValue = "[{\"title\": \"Default Title\", \"points\": 10}]")
    private ArrayList<AcceptedSubmission> acceptedSubmissions;

    private String message;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public ArrayList<AcceptedSubmission> getAcceptedSubmissions() {
        return acceptedSubmissions;
    }

    public void setAcceptedSubmissions(final ArrayList<AcceptedSubmission> acceptedSubmissions) {
        this.acceptedSubmissions = acceptedSubmissions;
    }
}
