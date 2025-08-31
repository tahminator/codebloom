package com.patina.codebloom.common.db.models.questiontopic;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@Builder
@ToString
@EqualsAndHashCode
public class QuestionTopic {
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
}
