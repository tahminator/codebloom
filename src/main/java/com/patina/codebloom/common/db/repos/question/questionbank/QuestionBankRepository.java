package com.patina.codebloom.common.db.repos.question.questionbank;

import java.util.List;

import com.patina.codebloom.common.db.models.question.bank.QuestionBank;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;

public interface QuestionBankRepository {
    void createQuestion(QuestionBank question);

    QuestionBank getQuestionById(String id);

    QuestionBank getQuestionBySlug(String slug);

    QuestionBank updateQuestion(QuestionBank question);

    boolean deleteQuestionById(String id);

    QuestionBank getRandomQuestion();

    List<QuestionBank> getQuestionsByTopic(LeetcodeTopicEnum topic);

    List<QuestionBank> getQuestionsByDifficulty(QuestionDifficulty difficulty);
}
