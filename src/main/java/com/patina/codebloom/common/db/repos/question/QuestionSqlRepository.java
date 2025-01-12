package com.patina.codebloom.common.db.repos.question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.UUID;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.question.Question;
import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

public class QuestionSqlRepository implements QuestionRepository {

    DbConnection dbConnection;
    Connection conn;

    public QuestionSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    public Question createQuestion(Question question) {
        String sql = "INSERT INTO \"Question\" (id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\") VALUES (?, ?, ?, ?, ?, ?, ?)";

        question.setId(UUID.randomUUID().toString());

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(question.getId()));
            stmt.setObject(2, UUID.fromString(question.getUserId()));
            stmt.setString(3, question.getQuestionSlug());
            stmt.setObject(4, question.getQuestionDifficulty());
            stmt.setInt(5, question.getQuestionNumber());
            stmt.setString(6, question.getQuestionLink());

            if (question.getPointsAwarded().isPresent()) {
                stmt.setInt(7, question.getPointsAwarded().getAsInt());
            } else {
                stmt.setNull(7, java.sql.Types.INTEGER);
            }

            stmt.setString(8, question.getQuestionTitle());

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
        String sql = "SELECT id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\" FROM \"Question\" WHERE id = ?";

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
                    question = new Question(questionId, userId, questionSlug, questionDifficulty, questionNumber,
                            questionLink, pointsAwarded, questionTitle);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve question", e);
        }

        return question;
    }

    public ArrayList<Question> getQuestionsByUserId(String userId) {
        ArrayList<Question> questions = new ArrayList<>();
        String sql = "SELECT id, \"userId\", \"questionSlug\", \"questionDifficulty\", \"questionNumber\", \"questionLink\", \"pointsAwarded\", \"questionTitle\" FROM \"Question\" WHERE \"userId\" = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
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
                    Question question = new Question(questionId, userIdResult, questionSlug, questionDifficulty,
                            questionNumber,
                            questionLink, pointsAwarded, questionTitle);
                    questions.add(question);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions", e);
        }

        return questions;
    }

    public Question updateQuestion(Question inputQuestion) {
        String sql = "UPDATE \"Question\" SET \"userId\" = ?, \"questionSlug\" = ?, \"questionDifficulty\" = ?, \"questionNumber\" = ?, \"questionLink\" = ?, \"pointsAwarded\" = ?, \"questionTitle\" = ? WHERE id = ?";

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
            stmt.setObject(8, UUID.fromString(inputQuestion.getId()));

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
}
