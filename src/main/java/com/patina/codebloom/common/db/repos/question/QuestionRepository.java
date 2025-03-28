package com.patina.codebloom.common.db.repos.question;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question getQuestionById(String id);

    QuestionWithUser getQuestionWithUserById(String id);

    ArrayList<Question> getQuestionsByUserId(String userId, int page, int pageSize, String query);

    Question updateQuestion(Question question);

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);

    boolean questionExistsBySubmissionId(String submissionId);

    int getQuestionCountByUserId(String userId, String query);
}
