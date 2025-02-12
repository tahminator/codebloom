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
     * Optional for the case of future proofing. We might end up using AI to award some points, so there might be a case where we create the DB entry and then pass it to a message
     * queue to use AI and calculate a score.
     */
    private int pointsAwarded;

    private float acceptanceRate;

    private String discordName;
    private String leetcodeUsername;

    public QuestionOverride(final String id, final String userId, final String questionSlug, final String questionTitle,
            final QuestionDifficulty questionDifficulty, final int questionNumber, final String questionLink, final String description,
            final int pointsAwarded, final float acceptanceRate, final LocalDateTime createdAt, final LocalDateTime submittedAt) {
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

    public void setId(final String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public String getQuestionSlug() {
        return questionSlug;
    }

    public void setQuestionSlug(final String questionSlug) {
        this.questionSlug = questionSlug;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(final String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public QuestionDifficulty getQuestionDifficulty() {
        return questionDifficulty;
    }

    public void setQuestionDifficulty(final QuestionDifficulty questionDifficulty) {
        this.questionDifficulty = questionDifficulty;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(final int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public String getQuestionLink() {
        return questionLink;
    }

    public void setQuestionLink(final String questionLink) {
        this.questionLink = questionLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public int getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(final int pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public float getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(final float acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(final LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getDiscordName() {
        return discordName;
    }

    public void setDiscordName(final String discordName) {
        this.discordName = discordName;
    }

    public String getLeetcodeUsername() {
        return leetcodeUsername;
    }

    public void setLeetcodeUsername(final String leetcodeUsername) {
        this.leetcodeUsername = leetcodeUsername;
    }
}
