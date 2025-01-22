package com.patina.codebloom.common.dto.autogen;

import java.util.ArrayList;

import com.patina.codebloom.common.submissions.object.AcceptedSubmission;

import io.swagger.v3.oas.annotations.media.Schema;

public class __DO_NOT_USE_UNLESS_YOU_KNOW_WHAT_YOU_ARE_DOING_EXAMPLE_SUBMISSION_CHECK_SUCCESS_RESPONSE {
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

    public void setAcceptedSubmissions(ArrayList<AcceptedSubmission> acceptedSubmissions) {
        this.acceptedSubmissions = acceptedSubmissions;
    }
}
