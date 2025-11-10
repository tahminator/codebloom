package com.patina.codebloom.common.db.models.question.bank;

import java.time.OffsetDateTime;
import java.util.List;

import org.checkerframework.checker.units.qual.N;

import com.patina.codebloom.common.db.helper.annotations.JoinColumn;
import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;

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
public class QuestionBank {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String questionSlug;

    @NotNullColumn
    private QuestionDifficulty questionDifficulty;

    @NotNullColumn
    private String questionTitle;
    
    @NotNullColumn
    private int questionNumber;

    @NotNullColumn
    private String questionLink;

    @NotNullColumn
    private String description;

    @NotNullColumn
    private float acceptanceRate;

    @NotNullColumn
    private OffsetDateTime createdAt;

    /**
     * Join field, update/create with {@link QuestionTopicRepository}
     */
    @JoinColumn
    private List<QuestionTopic> topics;

}
