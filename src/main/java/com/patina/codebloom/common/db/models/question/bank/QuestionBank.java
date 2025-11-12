package com.patina.codebloom.common.db.models.question.bank;

import java.util.List;

import com.patina.codebloom.common.db.helper.annotations.JoinColumn;
import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;

public class QuestionBank {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String questionSlug;

    @NotNullColumn
    private QuestionDifficulty difficulty;

    @NotNullColumn
    private String questionTitle;

    @NotNullColumn
    private String description;

    @NotNullColumn
    private float acceptanceRate;

    /**
     * Join field, update/create with {@link QuestionTopicRepository}
     */
    @JoinColumn
    private List<QuestionTopic> topics;

}
