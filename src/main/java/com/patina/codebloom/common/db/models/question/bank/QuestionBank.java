package com.patina.codebloom.common.db.models.question.bank;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;

public class QuestionBank {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String questionSlug;

    @NotNullColumn
    private QuestionDifficulty difficulty;

    @NotNullColumn
    private LeetcodeTopicEnum topics;

    // @NotNullColumn
    // private 
}
