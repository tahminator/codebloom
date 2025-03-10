package com.patina.codebloom.website.leetcode.client.models;

import com.patina.codebloom.website.leetcode.models.QuestionDifficulty;

public class LeetcodePOTD {
    private String title;
    private String titleSlug;
    private QuestionDifficulty difficulty;

    public LeetcodePOTD(final String title, final String titleSlug, final QuestionDifficulty difficulty) {
        this.title = title;
        this.titleSlug = titleSlug;
        this.difficulty = difficulty;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getTitleSlug() {
        return titleSlug;
    }

    public void setTitleSlug(final String titleSlug) {
        this.titleSlug = titleSlug;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(final QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }
}
