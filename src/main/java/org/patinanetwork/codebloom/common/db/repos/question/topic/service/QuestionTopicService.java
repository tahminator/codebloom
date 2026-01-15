package org.patinanetwork.codebloom.common.db.repos.question.topic.service;

import java.util.Set;
import org.patinanetwork.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import org.springframework.stereotype.Service;

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
