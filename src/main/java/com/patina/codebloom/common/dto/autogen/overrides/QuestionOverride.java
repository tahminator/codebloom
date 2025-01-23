package com.patina.codebloom.common.dto.autogen.overrides;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

public class QuestionOverride {
    private String id;
    private String userId;

    private String questionSlug;
    private String questionTitle;

    private QuestionDifficulty questionDifficulty;
    private int questionNumber;
    private String questionLink;

    private String description;

    /**
     * Optional for the case of future proofing. We might end up using AI to award
     * some points, so there might be a case where we create the DB entry and then
     * pass it to a message queue to use AI and calculate a score.
     */
    private int pointsAwarded;

    private float acceptanceRate;

    public QuestionOverride(String id, String userId, String questionSlug, String questionTitle,
            QuestionDifficulty questionDifficulty, int questionNumber, String questionLink, String description,
            int pointsAwarded, float acceptanceRate, LocalDateTime createdAt, LocalDateTime submittedAt) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionTitle = questionTitle;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.acceptanceRate = acceptanceRate;
        this.createdAt = createdAt;
        this.submittedAt = submittedAt;
    }

    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

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

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionDifficulty getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(QuestionDifficulty questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionLink() {
        return questionLink;
    }

    public void setQuestionLink(String questionLink) {
        this.questionLink = questionLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public float getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(float acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
