package com.patina.codebloom.common.db.repos.question.topic;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;
import java.util.List;

public interface QuestionTopicRepository {
    List<QuestionTopic> findQuestionTopicsByQuestionId(String questionId);

    List<QuestionTopic> findQuestionTopicsByQuestionBankId(String questionBankId);

    QuestionTopic findQuestionTopicById(String id);

    QuestionTopic findQuestionTopicByQuestionIdAndTopicEnum(String questionId, LeetcodeTopicEnum topicEnum);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param questionTopic - required fields:
     *     <ul>
     *       <li>questionId
     *       <li>topicTag
     *       <li>topic
     *       <li>topicSlug
     *     </ul>
     */
    void createQuestionTopic(QuestionTopic questionTopic);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param questionTopic - overridden fields:
     *     <ul>
     *       <li>questionId
     *       <li>topicTag
     *       <li>topic
     *       <li>topicSlug
     *     </ul>
     */
    boolean updateQuestionTopicById(QuestionTopic questionTopic);

    boolean deleteQuestionTopicById(String id);
}
