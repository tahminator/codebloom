package com.patina.codebloom.website.leetcode.client.model;

import java.time.LocalDateTime;

public class LeetcodeSubmission {
    private int id;

    private String title;
    private String titleSlug;
    private LocalDateTime timestamp;
    private String statusDisplay;

    public LeetcodeSubmission(final int id, final String title, final String titleSlug, final LocalDateTime timestamp,
                    final String statusDisplay) {
        this.id = id;
        this.title = title;
        this.titleSlug = titleSlug;
        this.timestamp = timestamp;
        this.statusDisplay = statusDisplay;
    }

    public int getId() {
        return id;
    }

    public void setId(final int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(final String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(final String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

}
