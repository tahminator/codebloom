package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;
import java.util.OptionalInt;

public class Question {
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
    private OptionalInt pointsAwarded;

    private float acceptanceRate;

    private LocalDateTime createdAt;
    private LocalDateTime submittedAt;

    private String runtime;
    private String memory;
    private String code;
    private String language;

    /*
     * No ID generated yet; brand new Question.
     */
    public Question(String userId, String questionSlug, QuestionDifficulty questionDifficulty, int questionNumber,
            String questionLink, String questionTitle, String description, OptionalInt pointsAwarded,
            float acceptanceRate, LocalDateTime submittedAt) {
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

    public Question(String userId, String questionSlug, QuestionDifficulty questionDifficulty, int questionNumber,
            String questionLink, String questionTitle, String description, OptionalInt pointsAwarded,
            float acceptanceRate, LocalDateTime submittedAt, String runtime, String memory, String code,
            String language) {
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
    }

    public Question(String id, String userId, String questionSlug, QuestionDifficulty questionDifficulty,
            int questionNumber,
            String questionLink, OptionalInt pointsAwarded, String questionTitle, String description,
            float acceptanceRate, LocalDateTime createdAt, LocalDateTime submittedAt, String runtime, String memory,
            String code,
            String language) {
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
    }

    public Question(String id, String userId, String questionSlug, QuestionDifficulty questionDifficulty,
            int questionNumber,
            String questionLink, OptionalInt pointsAwarded, String questionTitle, String description,
            float acceptanceRate, LocalDateTime createdAt, LocalDateTime submittedAt) {
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

    public OptionalInt getPointsAwarded() {
        return pointsAwarded;
    }

    public void setPointsAwarded(OptionalInt pointsAwarded) {
        this.pointsAwarded = pointsAwarded;
    }

    public String getQuestionTitle() {
        return questionTitle;
    }

    public void setQuestionTitle(String questionTitle) {
        this.questionTitle = questionTitle;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

}