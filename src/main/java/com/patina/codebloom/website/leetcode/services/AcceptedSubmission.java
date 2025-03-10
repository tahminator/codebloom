package com.patina.codebloom.website.leetcode.services;

public class AcceptedSubmission {
    private String title;
    private int points;

    public AcceptedSubmission(final String title, final int points) {
        this.title = title;
        this.points = points;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(final int points) {
        this.points = points;
    }
}
