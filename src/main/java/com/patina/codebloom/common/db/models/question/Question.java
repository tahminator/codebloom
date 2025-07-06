package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;

public class Question {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String userId;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionSlug;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionTitle;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private QuestionDifficulty questionDifficulty;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int questionNumber;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionLink;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String description;

    /**
     * Optional for the case of future proofing. We might end up using AI to award
     * some points, so there might be a case where we create the DB entry and then
     * pass it to a message queue to use AI and calculate a score.
     */
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private Integer pointsAwarded;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private float acceptanceRate;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime submittedAt;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String runtime;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String memory;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String code;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String language;

    // Not every submission will have this.
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
    private String submissionId;

    /*
     * No ID generated yet; brand new Question.
     */
    public Question(final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty, final int questionNumber,
                    final String questionLink, final String questionTitle, final String description, final Integer pointsAwarded,
                    final float acceptanceRate, final LocalDateTime submittedAt) {
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.questionTitle = questionTitle;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.acceptanceRate = acceptanceRate;
        this.createdAt = null;
        this.submittedAt = submittedAt;
    }

    public Question(final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty, final int questionNumber,
                    final String questionLink, final String questionTitle, final String description, final Integer pointsAwarded,
                    final float acceptanceRate, final LocalDateTime submittedAt, final String runtime, final String memory, final String code,
                    final String language, final String submissionId) {
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.questionTitle = questionTitle;
        this.description = description;
        this.pointsAwarded = pointsAwarded;
        this.acceptanceRate = acceptanceRate;
        this.createdAt = null;
        this.submittedAt = submittedAt;
        this.runtime = runtime;
        this.memory = memory;
        this.code = code;
        this.language = language;
        this.submissionId = submissionId;
    }

    public Question(final String id, final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty,
                    final int questionNumber, final String questionLink, final Integer pointsAwarded, final String questionTitle,
                    final String description, final float acceptanceRate, final LocalDateTime createdAt, final LocalDateTime submittedAt,
                    final String runtime, final String memory, final String code, final String language, final String submissionId) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.pointsAwarded = pointsAwarded;
        this.questionTitle = questionTitle;
        this.description = description;
        this.acceptanceRate = acceptanceRate;
        this.createdAt = createdAt;
        this.submittedAt = submittedAt;
        this.runtime = runtime;
        this.memory = memory;
        this.code = code;
        this.language = language;
        this.submissionId = submissionId;
    }

    public Question(final String id, final String userId, final String questionSlug, final QuestionDifficulty questionDifficulty,
                    final int questionNumber, final String questionLink, final Integer pointsAwarded, final String questionTitle,
                    final String description, final float acceptanceRate, final LocalDateTime createdAt, final LocalDateTime submittedAt, final String submissionId) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.pointsAwarded = pointsAwarded;
        this.questionTitle = questionTitle;
        this.description = description;
        this.acceptanceRate = acceptanceRate;
        this.createdAt = createdAt;
        this.submittedAt = submittedAt;
        this.submissionId = submissionId;
    }

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

    public Integer getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(final Integer pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(final String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
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

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(final String runtime) {
        this.runtime = runtime;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(final String memory) {
        this.memory = memory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(final String submissionId) {
        this.submissionId = submissionId;
    }
}
