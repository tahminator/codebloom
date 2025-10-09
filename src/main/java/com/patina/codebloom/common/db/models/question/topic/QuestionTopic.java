package com.patina.codebloom.common.db.models.question.topic;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class QuestionTopic {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String questionId;

    @NotNullColumn
    private String topicSlug;

    @NotNullColumn
    private LeetcodeTopicEnum topic;

    @NotNullColumn
    private LocalDateTime createdAt;
}
