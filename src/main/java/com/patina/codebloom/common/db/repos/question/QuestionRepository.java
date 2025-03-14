package com.patina.codebloom.common.db.repos.question;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.db.models.user.UserWithQuestions;

public interface QuestionRepository {
    Question createQuestion(Question question);

    Question getQuestionById(String id);

    QuestionWithUser getQuestionWithUserById(String id);

    UserWithQuestions getQuestionsByUserId(String userId, int page, int pageSize, String query);

    Question updateQuestion(Question question);

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);

    boolean questionExistsBySubmissionId(String submissionId);

    int getQuestionCountByUserId(String userId, String query);
}
