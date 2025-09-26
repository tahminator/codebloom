package com.patina.codebloom.common.db.repos.question.topic.service;

import java.util.Set;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;

public class QuestionTopicService {

    public LeetcodeTopicEnum stringtoEnum(final String topic) {
        try {
            return LeetcodeTopicEnum.valueOf(topic);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public LeetcodeTopicEnum[] stringsToEnums(final Set<String> topics) {
        return topics.stream()
                        .map(LeetcodeTopicEnum::fromValue)
                        .toArray(LeetcodeTopicEnum[]::new);
    }
}
