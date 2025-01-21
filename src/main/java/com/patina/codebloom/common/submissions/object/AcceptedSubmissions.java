package com.patina.codebloom.common.submissions.object;

import java.util.ArrayList;

public class AcceptedSubmissions {
    private ArrayList<String> acceptedSubmissions;

    public AcceptedSubmissions() {
        this.acceptedSubmissions = new ArrayList<String>();
    }

    public ArrayList<String> getAcceptedSubmissions() {
        return acceptedSubmissions;
    }

    public void addAcceptedSubmission(String acceptedSubmission) {
        this.acceptedSubmissions.add(acceptedSubmission);
    }

    public void clearAcceptedSubmissions() {
        this.acceptedSubmissions.clear();
    }
}
