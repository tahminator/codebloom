package org.patinanetwork.codebloom.common.leetcode.models;

import org.patinanetwork.codebloom.common.db.models.question.QuestionDifficulty;

public class POTD {

    private String title;
    private String titleSlug;
    private QuestionDifficulty difficulty;

    public POTD(final String title, final String titleSlug, final QuestionDifficulty difficulty) {
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
