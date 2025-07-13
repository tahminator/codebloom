package com.patina.codebloom.common.leetcode.models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LeetcodeQuestion {
    private String link;
    private int questionId;
    private String questionTitle;
    private String titleSlug;
    private String difficulty;
    private String question;
    private float acceptanceRate;
}
