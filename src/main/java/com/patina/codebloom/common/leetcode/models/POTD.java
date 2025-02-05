package com.patina.codebloom.common.leetcode.models;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

public class POTD {
    private String title;
    private String titleSlug;
    private QuestionDifficulty difficulty;

    public POTD(String title, String titleSlug, QuestionDifficulty difficulty) {
        this.title = title;
        this.titleSlug = titleSlug;
        this.difficulty = difficulty;
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

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
