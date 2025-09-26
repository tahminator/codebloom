package com.patina.codebloom.common.db.repos.question.topic.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;

@Service
public class QuestionTopicService {

    public LeetcodeTopicEnum stringtoEnum(final String topic) {
        try {
            return LeetcodeTopicEnum.valueOf(topic);
        } catch (IllegalArgumentException e) {
            return LeetcodeTopicEnum.UNKNOWN;
        }
    }

    public LeetcodeTopicEnum[] stringsToEnums(final Set<String> topics) {
        return topics.stream()
                        .map(LeetcodeTopicEnum::valueOf)
                        .toArray(LeetcodeTopicEnum[]::new);
    }

}
