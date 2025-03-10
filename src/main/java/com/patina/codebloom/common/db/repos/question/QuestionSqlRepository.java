package com.patina.codebloom.common.db.repos.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import com.patina.codebloom.common.db.models.question.QuestionWithUser;

@Component
public class QuestionSqlRepository implements QuestionRepository {

    private Connection conn;

    public QuestionSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

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
                        language
                    )
                    VALUES
                        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        question.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(question.getId()));
            stmt.setObject(2, UUID.fromString(question.getUserId()));
            stmt.setString(3, question.getQuestionSlug());
            stmt.setObject(4, question.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt(5, question.getQuestionNumber());
            stmt.setString(6, question.getQuestionLink());

            if (question.getPointsAwarded().isPresent()) {
                stmt.setInt(7, question.getPointsAwarded().getAsInt());
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
                        language
                    FROM
                        "Question"
                    WHERE
                        id = ?
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
                    OptionalInt pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = OptionalInt.of(points);
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

                    question = new Question(questionId, userId, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt, runtime, memory, code, language);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

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
                        u."discordName",
                        u."leetcodeUsername",
                        u."nickname"
                    FROM "Question" q
                    LEFT JOIN "User" u on q."userId" = u.id
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
                    OptionalInt pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = OptionalInt.of(points);
                    }
                    var questionTitle = rs.getString("questionTitle");
                    var description = rs.getString("description");
                    var acceptanceRate = rs.getFloat("acceptanceRate");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var runtime = rs.getString("runtime");
                    var memory = rs.getString("memory");
                    var code = rs.getString("code");
                    var language = rs.getString("language");
                    question = new QuestionWithUser(questionId, userId, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle, description, acceptanceRate,
                            createdAt, submittedAt, discordName, leetcodeUsername, runtime, memory, code, language, nickname);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    /**
     * TODO - Change this to be UserWithQuestions instead.
     */
    public ArrayList<QuestionWithUser> getQuestionsByUserId(final String userId, final int page, final int pageSize) {

        ArrayList<QuestionWithUser> questions = new ArrayList<>();
        String sql = """
                SELECT
                    u."discordName",
                    u."leetcodeUsername",
                    u."nickname",
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
                    q.language
                FROM
                    "Question" q
                LEFT JOIN
                    "User" u on q."userId" = u.id
                WHERE
                    "userId" = ?
                ORDER BY "submittedAt" DESC
                LIMIT ? OFFSET ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));

            stmt.setInt(2, pageSize);
            stmt.setInt(3, (page - 1) * pageSize);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var questionId = rs.getString("id");
                    var userIdResult = rs.getString("userId");
                    var questionSlug = rs.getString("questionSlug");
                    var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
                    var questionNumber = rs.getInt("questionNumber");
                    var questionLink = rs.getString("questionLink");
                    int points = rs.getInt("pointsAwarded");
                    OptionalInt pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = OptionalInt.of(points);
                    }
                    var questionTitle = rs.getString("questionTitle");
                    var description = rs.getString("description");
                    var acceptanceRate = rs.getFloat("acceptanceRate");
                    var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
                    var submittedAt = rs.getTimestamp("submittedAt").toLocalDateTime();
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var runtime = rs.getString("runtime");
                    var memory = rs.getString("memory");
                    var code = rs.getString("code");
                    var language = rs.getString("language");
                    QuestionWithUser question = new QuestionWithUser(questionId, userIdResult, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle,
                            description, acceptanceRate, createdAt, submittedAt, discordName, leetcodeUsername, runtime, memory, code, language, nickname);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return questions;
    }

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
                        language = ?
                    WHERE
                        id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputQuestion.getUserId()));
            stmt.setString(2, inputQuestion.getQuestionSlug());
            stmt.setObject(3, inputQuestion.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt(4, inputQuestion.getQuestionNumber());
            stmt.setString(5, inputQuestion.getQuestionLink());

            if (inputQuestion.getPointsAwarded().isPresent()) {
                stmt.setInt(6, inputQuestion.getPointsAwarded().getAsInt());
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
            stmt.setObject(15, UUID.fromString(inputQuestion.getId()));

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
                        language
                    FROM
                        "Question"
                    WHERE
                        "questionSlug" = ?
                        AND "userId" = ?
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
                    OptionalInt pointsAwarded;
                    if (rs.wasNull()) {
                        pointsAwarded = null;
                    } else {
                        pointsAwarded = OptionalInt.of(points);
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
                    question = new Question(questionId, userId, questionSlug, questionDifficulty, questionNumber, questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt, runtime, memory, code, language);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    public int getQuestionCountByUserId(final String userId) {
        String sql = """
                SELECT
                    COUNT(*)
                FROM
                    "Question"
                WHERE
                    "userId" = ?
                """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
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
}
