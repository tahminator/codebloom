package com.patina.codebloom.common.db.repos.questiontopic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.questiontopic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.questiontopic.QuestionTopic;

@Component
public class QuestionTopicSqlRepository implements QuestionTopicRepository {
    private final Connection conn;

    private QuestionTopic mapResultSetToQuestionTopic(final ResultSet resultSet) throws SQLException {
        return QuestionTopic.builder()
                        .id(resultSet.getString("id"))
                        .questionId(resultSet.getString("questionId"))
                        .topicSlug(resultSet.getString("topicSlug"))
                        .topic(LeetcodeTopicEnum.fromValue(resultSet.getString("topic")))
                        .build();
    }

    public QuestionTopicSqlRepository(final DbConnection connection) {
        this.conn = connection.getConn();
    }

    @Override
    public List<QuestionTopic> findQuestionTopicsByQuestionId(final String questionId) {
        List<QuestionTopic> result = new ArrayList<>();

        String sql = """
                            SELECT
                                id,
                                "questionId",
                                "topicSlug",
                                "topic"
                            FROM
                                "QuestionTopic" qt
                            WHERE
                                qt."questionId" = :questionId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("questionId", questionId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestionTopic(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find question topics by question ID", e);
        }
    }

    @Override
    public QuestionTopic findQuestionTopicById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findQuestionTopicById'");
    }

    @Override
    public QuestionTopic findQuestionTopicByQuestionIdAndTopicEnum(String questionId, LeetcodeTopicEnum topicEnum) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findQuestionTopicByQuestionIdAndTopicEnum'");
    }

    @Override
    public void createQuestionTopic(QuestionTopic questionTopic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createQuestionTopic'");
    }

    @Override
    public boolean updateQuestionTopicById(QuestionTopic questionTopic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateQuestionTopicById'");
    }

    @Override
    public boolean deleteQuestionTopicById(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteQuestionTopicById'");
    }

}
