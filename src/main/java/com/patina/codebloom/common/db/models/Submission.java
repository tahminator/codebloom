package com.patina.codebloom.common.db.models;

public class Submission {
    private String id;
    private String leetcodeUsername;
    private String questionSlug;
    private String timestamp;
    private String statusDisplay;
    private String lang;

    public Submission(String id, String leetcodeUsername, String questionSlug, String timestamp, String statusDisplay,
            String lang) {
        this.id = id;
        this.leetcodeUsername = leetcodeUsername;
        this.questionSlug = questionSlug;
        this.timestamp = timestamp;
        this.statusDisplay = statusDisplay;
        this.lang = lang;
    }

    public Submission() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }

    public String getQuestionSlug() {
        return questionSlug;
    }

    public void setQuestionSlug(String questionSlug) {
        this.questionSlug = questionSlug;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatusDisplay() {
        return statusDisplay;
    }

    public void setStatusDisplay(String status) {
        this.statusDisplay = status;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String language) {
        this.lang = language;
    }

}
