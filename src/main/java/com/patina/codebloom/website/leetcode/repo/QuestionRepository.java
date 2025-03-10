package com.patina.codebloom.website.leetcode.repo;

import java.util.ArrayList;

import com.patina.codebloom.website.leetcode.model.Question;
import com.patina.codebloom.website.leetcode.model.QuestionWithUser;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question getQuestionById(String id);

    QuestionWithUser getQuestionWithUserById(String id);

    ArrayList<QuestionWithUser> getQuestionsByUserId(String userId, int page, int pageSize);

    Question updateQuestion(Question question);

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);

    int getQuestionCountByUserId(String userId);
}
