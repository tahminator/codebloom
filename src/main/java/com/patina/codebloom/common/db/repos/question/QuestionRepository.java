package com.patina.codebloom.common.db.repos.question;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.question.Question;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question getQuestionById(String id);

    ArrayList<Question> getQuestionsByUserId(String userId, int start, int end);

    Question updateQuestion(Question question);

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);
}
