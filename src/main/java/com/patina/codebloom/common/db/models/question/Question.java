package com.patina.codebloom.common.db.models.question;

import java.time.LocalDateTime;
import java.util.List;

import com.patina.codebloom.common.db.helper.annotations.JoinColumn;
import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Question {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String userId;

    @NotNullColumn
    private String questionSlug;

    @NotNullColumn
    private String questionTitle;

    @NotNullColumn
    private QuestionDifficulty questionDifficulty;

    @NotNullColumn
    private int questionNumber;

    @NotNullColumn
    private String questionLink;

    @NullColumn
    private String description;

    /**
     * Optional for the case of future proofing. We might end up using AI to award
     * some points, so there might be a case where we create the DB entry and then
     * pass it to a message queue to use AI and calculate a score.
     */
    @NullColumn
    private Integer pointsAwarded;

    @NotNullColumn
    private float acceptanceRate;

    @NotNullColumn
    private LocalDateTime createdAt;

    @NotNullColumn
    private LocalDateTime submittedAt;

    @NullColumn
    private String runtime;

    @NullColumn
    private String memory;

    @NullColumn
    private String code;

    @NullColumn
    private String language;

    // Not every submission will have this.
    @NullColumn
    private String submissionId;

    /**
     * Join field, update/create with {@link QuestionTopicRepository}
     */
    @JoinColumn
    private List<QuestionTopic> topics;
}
