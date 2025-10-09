package com.patina.codebloom.common.dto.question.topic;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class QuestionTopicDto {
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String questionId;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private String topicSlug;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LeetcodeTopicEnum topic;
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDateTime createdAt;

    public static QuestionTopicDto fromQuestionTopic(final QuestionTopic questionTopic) {
        return QuestionTopicDto.builder()
                        .id(questionTopic.getId())
                        .questionId(questionTopic.getQuestionId())
                        .topicSlug(questionTopic.getTopicSlug())
                        .topic(questionTopic.getTopic())
                        .createdAt(questionTopic.getCreatedAt())
                        .build();
    }
}
