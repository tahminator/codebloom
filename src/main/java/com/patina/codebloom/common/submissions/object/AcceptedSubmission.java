package com.patina.codebloom.common.submissions.object;

public class AcceptedSubmission {
    private String title;
    private int points;

    public AcceptedSubmission(String title, int points) {
        this.title = title;
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
