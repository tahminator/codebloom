package com.patina.codebloom.common.db.models.question.topic;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@ToString
public class QuestionTopic {
    @NotNullColumn
    private String id;

    @NullColumn
    private String questionId;

    @NullColumn
    private String questionBankId;

    @NotNullColumn
    private String topicSlug;

    @NotNullColumn
    private LeetcodeTopicEnum topic;

    @NotNullColumn
    private LocalDateTime createdAt;
}
