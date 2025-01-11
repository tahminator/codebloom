package com.patina.codebloom.common.db.models;

public class Question {
    private String id;
    private String userId;

    private String questionSlug;
    private String questionDifficulty;
    private String questionId;
    private String questionLink;

    private int pointsAwarded;

    /*
     * No ID generated yet; brand new Question.
     */
    public Question(String userId, String questionSlug, String questionDifficulty, String questionId,
            String questionLink) {
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionId = questionId;
        this.questionLink = questionLink;
    }

    public Question(String id, String userId, String questionSlug, String questionDifficulty, String questionId,
            String questionLink) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionId = questionId;
        this.questionLink = questionLink;
    }

    public Question(String id, String userId, String questionSlug, String questionDifficulty, String questionId,
            String questionLink, int pointsAwarded) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionId = questionId;
        this.questionLink = questionLink;
        this.pointsAwarded = pointsAwarded;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestionSlug() {
        return questionSlug;
    }

    public void setQuestionSlug(String questionSlug) {
        this.questionSlug = questionSlug;
    }

    public String getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(String questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionLink() {
        return questionLink;
    }

    public void setQuestionLink(String questionLink) {
        this.questionLink = questionLink;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }
}
