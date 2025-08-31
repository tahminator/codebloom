package com.patina.codebloom.common.leetcode.models;

import lombok.ToString;

@ToString
public class LeetcodeQuestion {
    private String link;
    private int questionId;
    private String questionTitle;
    private String titleSlug;
    private String difficulty;
    private String question;
    private float acceptanceRate;

    public LeetcodeQuestion(final String link, final int questionId, final String questionTitle, final String titleSlug,
                    final String difficulty, final String question, final float acceptanceRate) {
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

    public void setLink(final String link) {
        this.link = link;
    }

    public int getQuestionId() {
        return questionId;
    }

    public void setQuestionId(final int questionId) {
        this.questionId = questionId;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(final String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(final String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(final String difficulty) {
        this.difficulty = difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(final String question) {
        this.question = question;
    }

    public float getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(final float acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }
}
