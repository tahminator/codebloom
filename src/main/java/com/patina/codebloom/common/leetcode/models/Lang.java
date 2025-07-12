package com.patina.codebloom.common.leetcode.models;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class Lang {
    private String name;
    private String verboseName;
}
