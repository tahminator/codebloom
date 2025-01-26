package com.patina.codebloom.common.leetcode.models;

import java.time.LocalDateTime;

public class LeetcodeSubmission {
    private int id;

    private String title;
    private String titleSlug;
    private LocalDateTime timestamp;
    private String statusDisplay;

    public LeetcodeSubmission(int id, String title, String titleSlug, LocalDateTime timestamp, String statusDisplay) {
        this.id = id;
        this.title = title;
        this.titleSlug = titleSlug;
        this.timestamp = timestamp;
        this.statusDisplay = statusDisplay;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String statusDisplay) {
        this.statusDisplay = statusDisplay;
    }

}
