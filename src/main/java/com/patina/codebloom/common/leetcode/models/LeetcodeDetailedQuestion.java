package com.patina.codebloom.common.leetcode.models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LeetcodeDetailedQuestion {
    private int runtime;
    private String runtimeDisplay;
    private float runtimePercentile;
    private int memory;
    private String memoryDisplay;
    private float memoryPercentile;
    private String code;
    private Lang lang;
}
