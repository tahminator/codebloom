package com.patina.codebloom.common.db.repos.question;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;
import com.patina.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import com.patina.codebloom.common.db.repos.question.topic.service.QuestionTopicService;
import com.patina.codebloom.common.db.repos.user.UserRepository;

@Component
public class QuestionSqlRepository implements QuestionRepository {

    private Connection conn;
    private final UserRepository userRepository;
    private final QuestionTopicRepository questionTopicRepository;
    private final QuestionTopicService questionTopicService;

    private Question mapResultSetToQuestion(final ResultSet rs) throws SQLException {
        var questionId = rs.getString("id");
        var userId = rs.getString("userId");
        var questionSlug = rs.getString("questionSlug");
        var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
        var questionNumber = rs.getInt("questionNumber");
        var questionLink = rs.getString("questionLink");
        int points = rs.getInt("pointsAwarded");
        Integer pointsAwarded = rs.wasNull() ? null : points;
        var questionTitle = rs.getString("questionTitle");
        var description = rs.getString("description");
        var acceptanceRate = rs.getFloat("acceptanceRate");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
        var runtime = rs.getString("runtime");
        var memory = rs.getString("memory");
        var code = rs.getString("code");
        var language = rs.getString("language");
        var submissionId = rs.getString("submissionId");

        return Question.builder()
                        .id(questionId)
                        .userId(userId)
                        .questionSlug(questionSlug)
                        .questionDifficulty(questionDifficulty)
                        .questionNumber(questionNumber)
                        .questionLink(questionLink)
                        .pointsAwarded(pointsAwarded)
                        .questionTitle(questionTitle)
                        .description(description)
                        .acceptanceRate(acceptanceRate)
                        .createdAt(createdAt)
                        .submittedAt(submittedAt)
                        .runtime(runtime)
                        .memory(memory)
                        .code(code)
                        .language(language)
                        .submissionId(submissionId)
                        .topics(questionTopicRepository.findQuestionTopicsByQuestionId(questionId))
                        .build();
    }

    public QuestionSqlRepository(
                    final DbConnection dbConnection,
                    final UserRepository userRepository,
                    final QuestionTopicRepository questionTopicRepository,
                    final QuestionTopicService questionTopicService) {
        this.conn = dbConnection.getConn();
        this.userRepository = userRepository;
        this.questionTopicRepository = questionTopicRepository;
        this.questionTopicService = questionTopicService;
    }

    @Override
    public Question createQuestion(final Question question) {
        String sql = """
                            INSERT INTO "Question" (
                                id,
                                "userId",
                                "questionSlug",
                                "questionDifficulty",
                                "questionNumber",
                                "questionLink",
                                "pointsAwarded",
                                "questionTitle",
                                description,
                                "acceptanceRate",
                                "submittedAt",
                                runtime,
                                memory,
                                code,
                                language,
                                "submissionId"
                            )
                            VALUES
                                (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                        """;

        question.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(question.getId()));
            stmt.setObject(2, UUID.fromString(question.getUserId()));
            stmt.setString(3, question.getQuestionSlug());
            stmt.setObject(4, question.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt(5, question.getQuestionNumber());
            stmt.setString(6, question.getQuestionLink());

            if (question.getPointsAwarded() != null) {
                stmt.setInt(7, question.getPointsAwarded());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.setString(8, question.getQuestionTitle());
            stmt.setString(9, question.getDescription());

            stmt.setFloat(10, question.getAcceptanceRate());
            stmt.setObject(11, question.getSubmittedAt());
            stmt.setString(12, question.getRuntime());
            stmt.setString(13, question.getMemory());
            stmt.setString(14, question.getCode());
            stmt.setString(15, question.getLanguage());
            stmt.setString(16, question.getSubmissionId());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return getQuestionById(question.getId());
            } else {
                throw new RuntimeException("Failed to create question.");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create question", e);
        }
    }

    @Override
    public Question getQuestionById(final String id) {
        Question question = null;
        String sql = """
                            SELECT
                                id,
                                "userId",
                                "questionSlug",
                                "questionDifficulty",
                                "questionNumber",
                                "questionLink",
                                "pointsAwarded",
                                "questionTitle",
                                description,
                                "acceptanceRate",
                                "createdAt",
                                "submittedAt",
                                runtime,
                                memory,
                                code,
                                language,
                                "submissionId"
                            FROM
                                "Question"
                            WHERE
                                id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    question = mapResultSetToQuestion(rs);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    @Override
    public QuestionWithUser getQuestionWithUserById(final String id) {
        QuestionWithUser question = null;
        String sql = """
                            SELECT
                                q.id,
                                q."userId",
                                q."questionSlug",
                                q."questionDifficulty",
                                q."questionNumber",
                                q."questionLink",
                                q."pointsAwarded",
                                q."questionTitle",
                                q.description,
                                q."acceptanceRate",
                                q."createdAt",
                                q."submittedAt",
                                q.runtime,
                                q.memory,
                                q.code,
                                q.language,
                                q."submissionId"
                            FROM "Question" q
                            WHERE q.id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var questionId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var questionSlug = rs.getString("questionSlug");
                    var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
                    var questionNumber = rs.getInt("questionNumber");
                    var questionLink = rs.getString("questionLink");
                    int points = rs.getInt("pointsAwarded");
                    Integer pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = points;
                    }
                    var questionTitle = rs.getString("questionTitle");
                    var description = rs.getString("description");
                    var acceptanceRate = rs.getFloat("acceptanceRate");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
                    var runtime = rs.getString("runtime");
                    var memory = rs.getString("memory");
                    var code = rs.getString("code");
                    var language = rs.getString("language");
                    var submissionId = rs.getString("submissionId");

                    User user = userRepository.getUserById(userId);

                    question = QuestionWithUser.builder()
                                    .id(questionId)
                                    .userId(userId)
                                    .questionSlug(questionSlug)
                                    .questionDifficulty(questionDifficulty)
                                    .questionNumber(questionNumber)
                                    .questionLink(questionLink)
                                    .pointsAwarded(pointsAwarded)
                                    .questionTitle(questionTitle)
                                    .description(description)
                                    .acceptanceRate(acceptanceRate)
                                    .createdAt(createdAt)
                                    .submittedAt(submittedAt)
                                    .runtime(runtime)
                                    .memory(memory)
                                    .code(code)
                                    .language(language)
                                    .submissionId(submissionId)
                                    .discordName(user.getDiscordName())
                                    .leetcodeUsername(user.getLeetcodeUsername())
                                    .nickname(user.getNickname())
                                    .build();

                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    @Override
    public ArrayList<Question> getQuestionsByUserId(final String userId, final int page, final int pageSize, final String query, final boolean pointFilter, final Set<String> topics) {
        ArrayList<Question> questions = new ArrayList<>();
        String sql = """
                        SELECT *
                        FROM (
                            SELECT DISTINCT ON (q.id)
                                q.id,
                                q."userId",
                                q."questionSlug",
                                q."questionDifficulty",
                                q."questionNumber",
                                q."questionLink",
                                q."pointsAwarded",
                                q."questionTitle",
                                q.description,
                                q."acceptanceRate",
                                q."createdAt",
                                q."submittedAt",
                                q.runtime,
                                q.memory,
                                q.code,
                                q.language,
                                q."submissionId"
                            FROM
                                "Question" q
                            JOIN "User" u ON q."userId" = u.id
                            LEFT JOIN "QuestionTopic" t ON t."questionId" = q."id"
                            WHERE
                                q."userId" = ?
                                AND q."questionTitle" ILIKE ?
                                AND (NOT ? OR q."pointsAwarded" <> 0)
                                AND (
                                    ? = '{}'::"LeetcodeTopicEnum"[]
                                    OR t."topic" = ANY(?)
                                )
                            ORDER BY q.id, q."submittedAt" DESC
                        ) sub
                        ORDER BY "submittedAt" DESC
                        LIMIT ? OFFSET ?;
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
            stmt.setString(2, "%" + query + "%");
            stmt.setBoolean(3, pointFilter);

            LeetcodeTopicEnum[] topicEnums = questionTopicService.stringsToEnums(topics);

            String[] sqlValues = Arrays.stream(topicEnums)
                            .map(LeetcodeTopicEnum::getLeetcodeEnum)
                            .toArray(String[]::new);
            Array topicsArray = conn.createArrayOf("\"LeetcodeTopicEnum\"", sqlValues);
            stmt.setArray(4, topicsArray);
            stmt.setArray(5, topicsArray);
            stmt.setInt(6, pageSize);
            stmt.setInt(7, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var questionId = rs.getString("id");
                    var questionSlug = rs.getString("questionSlug");
                    var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
                    var questionNumber = rs.getInt("questionNumber");
                    var questionLink = rs.getString("questionLink");
                    int points = rs.getInt("pointsAwarded");
                    Integer pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = points;
                    }
                    var questionTitle = rs.getString("questionTitle");
                    var description = rs.getString("description");
                    var acceptanceRate = rs.getFloat("acceptanceRate");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
                    var runtime = rs.getString("runtime");
                    var memory = rs.getString("memory");
                    var code = rs.getString("code");
                    var language = rs.getString("language");
                    var submissionId = rs.getString("submissionId");
                    Question question = mapResultSetToQuestion(rs);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return questions;
    }

    @Override
    public Question updateQuestion(final Question inputQuestion) {
        String sql = """
                            UPDATE "Question"
                            SET
                                "userId" = ?,
                                "questionSlug" = ?,
                                "questionDifficulty" = ?,
                                "questionNumber" = ?,
                                "questionLink" = ?,
                                "pointsAwarded" = ?,
                                "questionTitle" = ?,
                                description = ?,
                                "acceptanceRate" = ?,
                                "submittedAt" = ?,
                                runtime = ?,
                                memory = ?,
                                code = ?,
                                language = ?,
                                "submissionId" = ?
                            WHERE
                                id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputQuestion.getUserId()));
            stmt.setString(2, inputQuestion.getQuestionSlug());
            stmt.setObject(3, inputQuestion.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt(4, inputQuestion.getQuestionNumber());
            stmt.setString(5, inputQuestion.getQuestionLink());

            if (inputQuestion.getPointsAwarded() != null) {
                stmt.setInt(6, inputQuestion.getPointsAwarded());
            } else {
                stmt.setNull(6, java.sql.Types.INTEGER);
            }
            stmt.setString(7, inputQuestion.getQuestionTitle());
            stmt.setString(8, inputQuestion.getDescription());
            stmt.setFloat(9, inputQuestion.getAcceptanceRate());
            stmt.setObject(10, inputQuestion.getSubmittedAt());
            stmt.setString(11, inputQuestion.getRuntime());
            stmt.setString(12, inputQuestion.getMemory());
            stmt.setString(13, inputQuestion.getCode());
            stmt.setString(14, inputQuestion.getLanguage());
            stmt.setString(15, inputQuestion.getSubmissionId());
            stmt.setObject(16, UUID.fromString(inputQuestion.getId()));

            stmt.executeUpdate();

            return getQuestionById(inputQuestion.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update question", e);
        }
    }

    @Override
    public boolean deleteQuestionById(final String id) {
        String sql = "DELETE FROM \"Question\" WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting session", e);
        }

    }

    @Override
    public Question getQuestionBySlugAndUserId(final String slug, final String inputtedUserId) {
        Question question = null;
        String sql = """
                            SELECT
                                id,
                                "userId",
                                "questionSlug",
                                "questionDifficulty",
                                "questionNumber",
                                "questionLink",
                                "pointsAwarded",
                                "questionTitle",
                                description,
                                "acceptanceRate",
                                "createdAt",
                                "submittedAt",
                                runtime,
                                memory,
                                code,
                                language,
                                "submissionId"
                            FROM
                                "Question"
                            WHERE
                                "questionSlug" = ?
                            AND
                                "userId" = ?
                            LIMIT 1
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, slug);
            stmt.setObject(2, UUID.fromString(inputtedUserId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var questionId = rs.getString("id");
                    var userId = rs.getString("userId");
                    var questionSlug = rs.getString("questionSlug");
                    var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
                    var questionNumber = rs.getInt("questionNumber");
                    var questionLink = rs.getString("questionLink");
                    int points = rs.getInt("pointsAwarded");
                    Integer pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = points;
                    }
                    var questionTitle = rs.getString("questionTitle");
                    var description = rs.getString("description");
                    var acceptanceRate = rs.getFloat("acceptanceRate");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
                    var runtime = rs.getString("runtime");
                    var memory = rs.getString("memory");
                    var code = rs.getString("code");
                    var language = rs.getString("language");
                    var submissionId = rs.getString("submissionId");
                    question = mapResultSetToQuestion(rs);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    @Override
    public int getQuestionCountByUserId(final String userId, final String query, final boolean pointFilter, final Set<String> topics) {
        String sql = """
                        SELECT
                            COUNT(DISTINCT q.id)
                        FROM
                            "Question" q
                        LEFT JOIN "QuestionTopic" qt ON qt."questionId" = q.id
                        WHERE
                            q."userId" = :userId
                        AND
                            q."questionTitle" ILIKE :title
                        AND
                            (NOT :pointFilter OR q."pointsAwarded" <> 0)
                        AND (
                            :topics = '{}'::"LeetcodeTopicEnum"[]
                            OR qt."topic" = ANY(:topics)
                        )
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));
            stmt.setString("title", "%" + query + "%");
            stmt.setBoolean("pointFilter", pointFilter);
            LeetcodeTopicEnum[] topicEnums = questionTopicService.stringsToEnums(topics);
            String[] sqlValues = Arrays.stream(topicEnums)
                            .map(LeetcodeTopicEnum::getLeetcodeEnum)
                            .toArray(String[]::new);
            Array topicsArray = conn.createArrayOf("\"LeetcodeTopicEnum\"", sqlValues);
            stmt.setArray("topics", topicsArray);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return 0;
    }

    @Override
    public boolean questionExistsBySubmissionId(final String submissionId) {
        String sql = """
                            SELECT
                                id
                            FROM
                                "Question"
                            WHERE
                                "submissionId" = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, submissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }
    }

    @Override
    public ArrayList<Question> getAllIncompleteQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        String sql = """
                        SELECT
                            *
                        FROM
                            "Question"
                        WHERE
                            ("runtime" IS NULL OR "runtime" = '')
                            OR ("memory" IS NULL OR "memory" = '')
                            OR ("code" is NULL OR "code" = '')
                            OR ("language" is NULL OR "language" = '')
                        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all incomplete questions", e);
        }

        return questions;
    }

    @Override
    public List<Question> getAllQuestionsWithNoTopics() {
        List<Question> result = new ArrayList<>();

        String sql = """
                        SELECT
                            q.id,
                            q."userId",
                            q."questionSlug",
                            q."questionDifficulty",
                            q."questionNumber",
                            q."questionLink",
                            q."pointsAwarded",
                            q."questionTitle",
                            q.description,
                            q."acceptanceRate",
                            q."createdAt",
                            q."submittedAt",
                            q.runtime,
                            q.memory,
                            q.code,
                            q.language,
                            q."submissionId"
                        FROM
                            "Question" q
                        WHERE NOT EXISTS (
                            SELECT 1
                            FROM "QuestionTopic" qt
                            WHERE qt."questionId" = q.id
                        );
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(mapResultSetToQuestion(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all questions with no topics", e);
        }
    }
}
