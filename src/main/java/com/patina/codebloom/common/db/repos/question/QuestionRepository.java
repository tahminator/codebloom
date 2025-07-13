package com.patina.codebloom.common.db.repos.question;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import java.util.ArrayList;

public interface QuestionRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param question - required fields:
     * <ul>
     * <li>userId</li>
     * <li>questionSlug</li>
     * <li>questionDifficulty</li>
     * <li>questionNumber</li>
     * <li>questionLink</li>
     * <li>questionTitle</li>
     * <li>description</li>
     * <li>pointsAwarded</li>
     * <li>acceptanceRate</li>
     * <li>submittedAt</li>
     * </ul>
     */
    Question createQuestion(Question question);

    Question getQuestionById(String id);

    QuestionWithUser getQuestionWithUserById(String id);

    ArrayList<Question> getQuestionsByUserId(String userId, int page, int pageSize, String query);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param question - overridden fields:
     * <ul>
     * <li>questionTitle</li>
     * <li>description</li>
     * <li>pointsAwarded</li>
     * <li>acceptanceRate</li>
     * <li>runtime</li>
     * <li>memory</li>
     * <li>code</li>
     * <li>language</li>
     * <li>submissionId</li>
     * </ul>
     * @return updated question if sucessful 
     */
    Question updateQuestion(Question question);

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);

    boolean questionExistsBySubmissionId(String submissionId);

    int getQuestionCountByUserId(String userId, String query);
}
