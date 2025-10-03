package com.patina.codebloom.common.db.repos.question.topic.service;

import java.util.Set;

import org.springframework.stereotype.Service;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;

@Service
public class QuestionTopicService {

    public LeetcodeTopicEnum[] stringsToEnums(final Set<String> topics) {
        return topics.stream()
                        .map(s -> {
                            try {
                                return LeetcodeTopicEnum.valueOf(s);
                            } catch (IllegalArgumentException e) {
                                return null;
                            }
                        })
                        .filter(java.util.Objects::nonNull)
                        .toArray(LeetcodeTopicEnum[]::new);
    }

}
