package com.patina.codebloom.common.db.repos.question.topic;

import java.util.List;

import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;

public interface QuestionTopicRepository {
    List<QuestionTopic> findQuestionTopicsByQuestionId(String questionId);
    
    List<QuestionTopic> findQuestionTopicsByQuestionBankId(String questionBankId);

    QuestionTopic findQuestionTopicById(String id);

    QuestionTopic findQuestionTopicByQuestionIdAndTopicEnum(String questionId, LeetcodeTopicEnum topicEnum);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     * 
     * @param questionTopic - required fields:
     * <ul>
     * <li>questionId</li>
     * <li>topicTag</li>
     * <li>topic</li>
     * <li>topicSlug</li>
     * </ul>
     */
    void createQuestionTopic(QuestionTopic questionTopic);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param questionTopic - overridden fields:
     * <ul>
     * <li>questionId</li>
     * <li>topicTag</li>
     * <li>topic</li>
     * <li>topicSlug</li>
     * </ul>
     */
    boolean updateQuestionTopicById(QuestionTopic questionTopic);

    boolean deleteQuestionTopicById(String id);
}
