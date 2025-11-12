package com.patina.codebloom.common.db.repos.question.topic;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.question.topic.QuestionTopic;

@Component
public class QuestionTopicSqlRepository implements QuestionTopicRepository {
    private final Connection conn;

    private QuestionTopic mapResultSetToQuestionTopic(final ResultSet resultSet) throws SQLException {
        return QuestionTopic.builder()
                        .id(resultSet.getString("id"))
                        .createdAt(resultSet.getTimestamp("createdAt").toLocalDateTime())
                        .questionId(resultSet.getString("questionId"))
                        .questionBankId(resultSet.getString("questionBankId"))
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
                                "questionBankId",
                                "topicSlug",
                                "createdAt",
                                "topic"
                            FROM
                                "QuestionTopic" qt
                            WHERE
                                qt."questionId" = :questionId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionId", UUID.fromString(questionId));

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
    public List<QuestionTopic> findQuestionTopicsByQuestionBankId(final String questionBankId) {
        List<QuestionTopic> result = new ArrayList<>();

        String sql = """
                            SELECT
                                id,
                                "questionId",
                                "questionBankId",
                                "topicSlug",
                                "createdAt",
                                "topic"
                            FROM
                                "QuestionTopic" qt
                            WHERE
                                qt."questionBankId" = :questionBankId
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionBankId", UUID.fromString(questionBankId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestionTopic(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find question topics by question bank ID", e);
        }
    }

    @Override
    public QuestionTopic findQuestionTopicById(final String id) {
        try {
            String sql = """
                                SELECT
                                    id,
                                    "questionId",
                                    "questionBankId",
                                    "topicSlug",
                                    "createdAt",
                                    "topic"
                                FROM
                                    "QuestionTopic" qt
                                WHERE
                                    qt.id = :id
                            """;

            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
                stmt.setObject("id", UUID.fromString(id));

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapResultSetToQuestionTopic(rs);
                    }
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get question topic by ID", e);
        }
    }

    @Override
    public QuestionTopic findQuestionTopicByQuestionIdAndTopicEnum(final String questionId, final LeetcodeTopicEnum topicEnum) {
        String sql = """
                            SELECT
                                id,
                                "questionId",
                                "questionBankId",
                                "topicSlug",
                                "createdAt",
                                "topic"
                            FROM
                                "QuestionTopic" qt
                            WHERE
                                qt."questionId" = :questionId
                            AND
                                qt.topic = :topic
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionId", UUID.fromString(questionId));
            stmt.setObject("topic", topicEnum.getLeetcodeEnum(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQuestionTopic(rs);
                }
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get question topic by ID", e);
        }
    }

    @Override
    public void createQuestionTopic(final QuestionTopic questionTopic) {
        String sql = """
                        INSERT INTO "QuestionTopic"
                            ("id", "questionId", "questionBankId", "topicSlug", "topic")
                        VALUES
                            (:id, :questionId, :questionBankId, :topicSlug, :topic)
                        RETURNING
                            "createdAt"
                    """;

        questionTopic.setId(UUID.randomUUID().toString());

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(questionTopic.getId()));
            
            if (questionTopic.getQuestionId() == null) {
                stmt.setNull("questionId", java.sql.Types.NULL);
            } else {
                stmt.setObject("questionId", UUID.fromString(questionTopic.getQuestionId()));
            }
            
            if (questionTopic.getQuestionBankId() == null) {
                stmt.setNull("questionBankId", java.sql.Types.NULL);
            } else {
                stmt.setObject("questionBankId", UUID.fromString(questionTopic.getQuestionBankId()));
            }

            stmt.setString("topicSlug", questionTopic.getTopicSlug());
            stmt.setObject("topic", questionTopic.getTopic().getLeetcodeEnum(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    questionTopic.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to create question topic", e);
        }
    }

    @Override
    public boolean updateQuestionTopicById(final QuestionTopic questionTopic) {
        String sql = """
                                        UPDATE
                                            "QuestionTopic"
                                        SET
                                            "questionId" = :questionId,
                                            "questionBankId" = :questionBankId,
                                            "topicSlug" = :topicSlug,
                                            "topic"  = :topic
                                        WHERE
                                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(questionTopic.getId()));

            if (questionTopic.getQuestionId() == null) {
                stmt.setNull("questionId", java.sql.Types.NULL);
            } else {
                stmt.setObject("questionId", UUID.fromString(questionTopic.getQuestionId()));
            }
            
            if (questionTopic.getQuestionBankId() == null) {
                stmt.setNull("questionBankId", java.sql.Types.NULL);
            } else {
                stmt.setObject("questionBankId", UUID.fromString(questionTopic.getQuestionBankId()));
            }

            stmt.setString("topicSlug", questionTopic.getTopicSlug());
            stmt.setObject("topic", questionTopic.getTopic().getLeetcodeEnum(), java.sql.Types.OTHER);

            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update question topic by ID", e);
        }
    }

    @Override
    public boolean deleteQuestionTopicById(final String id) {
        String sql = """
                        DELETE FROM
                            "QuestionTopic"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by tag ID", e);
        }
    }
}
