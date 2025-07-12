package com.patina.codebloom.common.leetcode.models;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LeetcodeSubmission {
    private int id;
    private String title;
    private String titleSlug;
    private LocalDateTime timestamp;
    private String statusDisplay;
}
