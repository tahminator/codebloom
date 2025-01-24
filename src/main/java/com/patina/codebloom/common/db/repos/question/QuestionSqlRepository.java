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

    DbConnection dbConnection;
    Connection conn;

    public QuestionSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    public Question createQuestion(Question question) {
        String sql = "INSERT INTO \"Question\" (id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\", description, \"acceptanceRate\", \"submittedAt\") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

    public Question getQuestionById(String id) {
        Question question = null;
        String sql = "SELECT id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\", description, \"acceptanceRate\", \"createdAt\", \"submittedAt\" FROM \"Question\" WHERE id = ?";

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
                    question = new Question(questionId, userId, questionSlug, questionDifficulty, questionNumber,
                            questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    public QuestionWithUser getQuestionWithUserById(String id) {
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
                        u."discordName",
                        u."leetcodeUsername"
                    FROM "Question" q
                    LEFT JOIN "User" u on q."userId" = u.id
                    WHERE q.id = ?
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println(rs.toString());
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
                    question = new QuestionWithUser(questionId, userId, questionSlug, questionDifficulty,
                            questionNumber,
                            questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt, discordName, leetcodeUsername);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    public ArrayList<Question> getQuestionsByUserId(String userId, int start, int end) {
        ArrayList<Question> questions = new ArrayList<>();
        String sql = "SELECT id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\", description, \"acceptanceRate\", \"createdAt\", \"submittedAt\" FROM \"Question\" WHERE \"userId\" = ? ORDER BY \"submittedAt\" DESC LIMIT ? OFFSET ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));

            stmt.setInt(2, end - start);
            stmt.setInt(3, start);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var questionId = rs.getString("id");
                    var userIdResult = rs.getString("userId");
                    var questionSlug = rs.getString("questionSlug");
                    var questionDifficulty = QuestionDifficulty
                            .valueOf(rs.getString("questionDifficulty"));
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
                    Question question = new Question(questionId, userIdResult, questionSlug, questionDifficulty,
                            questionNumber,
                            questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return questions;
    }

    public Question updateQuestion(Question inputQuestion) {
        String sql = "UPDATE \"Question\" SET \"userId\" = ?, \"questionSlug\" = ?, \"questionDifficulty\" = ?, \"questionNumber\" = ?, \"questionLink\" = ?, \"pointsAwarded\" = ?, \"questionTitle\" = ?, description = ?, \"acceptanceRate\" = ?, \"submittedAt\" = ? WHERE id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputQuestion.getUserId()));
            stmt.setString(2, inputQuestion.getQuestionSlug());
            stmt.setString(3, inputQuestion.getQuestionDifficulty().name());
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
            stmt.setObject(11, UUID.fromString(inputQuestion.getId()));

            stmt.executeUpdate();

            return getQuestionById(inputQuestion.getId());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update question", e);
        }
    }

    @Override
    public boolean deleteQuestionById(String id) {
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
    public Question getQuestionBySlugAndUserId(String slug, String inputtedUserId) {
        Question question = null;
        String sql = "SELECT id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\", description, \"acceptanceRate\", \"createdAt\", \"submittedAt\" FROM \"Question\" WHERE \"questionSlug\" = ? AND \"userId\" = ?";

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
                    question = new Question(questionId, userId, questionSlug, questionDifficulty, questionNumber,
                            questionLink, pointsAwarded, questionTitle, description, acceptanceRate, createdAt,
                            submittedAt);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }
}