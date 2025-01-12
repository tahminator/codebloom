package com.patina.codebloom.common.db.models.question;

import java.util.OptionalInt;

public class Question {
    private String id;
    private String userId;

    private String questionSlug;
    private String questionTitle;

    private QuestionDifficulty questionDifficulty;
    private int questionNumber;
    private String questionLink;

    private OptionalInt pointsAwarded;

    /*
     * No ID generated yet; brand new Question.
     */
    public Question(String userId, String questionSlug, QuestionDifficulty questionDifficulty, int questionNumber,
            String questionLink, String questionTitle) {
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.questionTitle = questionTitle;
    }

    public Question(String id, String userId, String questionSlug, QuestionDifficulty questionDifficulty,
            int questionNumber,
            String questionLink, OptionalInt pointsAwarded, String questionTitle) {
        this.id = id;
        this.userId = userId;
        this.questionSlug = questionSlug;
        this.questionDifficulty = questionDifficulty;
        this.questionNumber = questionNumber;
        this.questionLink = questionLink;
        this.pointsAwarded = pointsAwarded;
        this.questionTitle = questionTitle;
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
}
