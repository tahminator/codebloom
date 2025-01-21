package com.patina.codebloom.common.leetcode.models;

public class LeetcodeQuestion {
    public String link;
    public int questionId;
    public String questionTitle;
    public String titleSlug;
    public String difficulty;
    public String question;
    public float acceptanceRate;

    public LeetcodeQuestion(String link, int questionId, String questionTitle, String titleSlug, String difficulty,
            String question, float acceptanceRate) {
        this.link = link;
        this.questionId = questionId;
        this.questionTitle = questionTitle;
        this.titleSlug = titleSlug;
        this.difficulty = difficulty;
        this.question = question;
        this.acceptanceRate = acceptanceRate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public float getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(float acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }
}
