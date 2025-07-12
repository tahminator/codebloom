package com.patina.codebloom.common.leetcode.models;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class POTD {
    private String title;
    private String titleSlug;
    private QuestionDifficulty difficulty;
}
