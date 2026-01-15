package org.patinanetwork.codebloom.common.leetcode.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/** This class stores slugs as a string. For slugs that are mapped to be an enum, see `LeetcodeQuestionTopicTag` */
@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class LeetcodeTopicTag {

    private String name;
    private String slug;
}
