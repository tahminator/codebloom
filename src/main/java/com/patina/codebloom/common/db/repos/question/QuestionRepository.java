package com.patina.codebloom.common.db.repos.question;

import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

    ArrayList<Question> getQuestionsByUserId(
        String userId,
        int page,
        int pageSize,
        String query,
        boolean pointFilter,
        LeetcodeTopicEnum[] topics
    );

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

    /**
     * @note - Some fields may not be populated properly due to a service outage. So
     * this method looks for any Questions missing fields populated by LeetCode's
     * API.
     *
     * @return all questions missing either a runtime, memory, code, language, or
     * description
     */
    ArrayList<Question> getAllIncompleteQuestions();

    /**
     * @note - Special case that will do a reverse-lookup on `QuestionTopic` to
     * return all `Question` rows that do not have any assigned topics yet.
     */
    List<Question> getAllQuestionsWithNoTopics();

    /**
     * @note - Returns all incomplete questions with user information, ordered by
     * most recently submitted. Incomplete questions are those missing either a
     * runtime, memory, code, or language.
     *
     * @return all incomplete questions with user details, sorted by submittedAt
     * DESC
     */
    ArrayList<QuestionWithUser> getAllIncompleteQuestionsWithUser();

    boolean deleteQuestionById(String id);

    Question getQuestionBySlugAndUserId(String slug, String userId);

    boolean questionExistsBySubmissionId(String submissionId);

    int getQuestionCountByUserId(
        String userId,
        String query,
        boolean filterPoints,
        Set<String> topics
    );
}
