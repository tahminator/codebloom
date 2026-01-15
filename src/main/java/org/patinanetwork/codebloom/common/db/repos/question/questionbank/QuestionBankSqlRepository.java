package org.patinanetwork.codebloom.common.db.repos.question.questionbank;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.patinanetwork.codebloom.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codebloom.common.db.models.question.QuestionDifficulty;
import org.patinanetwork.codebloom.common.db.models.question.bank.QuestionBank;
import org.patinanetwork.codebloom.common.db.models.question.topic.LeetcodeTopicEnum;
import org.patinanetwork.codebloom.common.db.repos.question.topic.QuestionTopicRepository;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class QuestionBankSqlRepository implements QuestionBankRepository {

    private final DataSource ds;
    private final QuestionTopicRepository questionTopicRepository;

    public QuestionBankSqlRepository(final DataSource ds, final QuestionTopicRepository questionTopicRepository) {
        this.ds = ds;
        this.questionTopicRepository = questionTopicRepository;
    }

    private QuestionBank mapResultSetToQuestion(final ResultSet rs) throws SQLException {
        var questionBankId = rs.getString("id");
        var questionSlug = rs.getString("questionSlug");
        var questionDifficulty = QuestionDifficulty.valueOf(rs.getString("questionDifficulty"));
        var questionNumber = rs.getInt("questionNumber");
        var questionLink = rs.getString("questionLink");
        var questionTitle = rs.getString("questionTitle");
        var description = rs.getString("description");
        var acceptanceRate = rs.getFloat("acceptanceRate");
        var createdAt = StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class));

        return QuestionBank.builder()
                .id(questionBankId)
                .questionSlug(questionSlug)
                .questionDifficulty(questionDifficulty)
                .questionNumber(questionNumber)
                .questionLink(questionLink)
                .questionTitle(questionTitle)
                .description(description)
                .acceptanceRate(acceptanceRate)
                .createdAt(createdAt)
                .topics(questionTopicRepository.findQuestionTopicsByQuestionBankId(questionBankId))
                .build();
    }

    @Override
    public void createQuestion(final QuestionBank question) {
        String sql = """
                INSERT INTO "QuestionBank" (
                    id,
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "questionTitle",
                    description,
                    "acceptanceRate"
                )
                VALUES
                    (:id, :slug, :difficulty, :number, :link, :title, :desc, :ac)
            """;

        question.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(question.getId()));
            stmt.setString("slug", question.getQuestionSlug());
            stmt.setObject("difficulty", question.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt("number", question.getQuestionNumber());
            stmt.setString("link", question.getQuestionLink());
            stmt.setString("title", question.getQuestionTitle());
            stmt.setString("desc", question.getDescription());
            stmt.setObject("ac", question.getAcceptanceRate());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create question", e);
        }
    }

    @Override
    public QuestionBank getQuestionById(final String id) {
        QuestionBank question = null;
        String sql = """
                SELECT
                    id,
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt"
                FROM
                    "QuestionBank"
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
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
    public QuestionBank getQuestionBySlug(final String slug) {
        QuestionBank question = null;
        String sql = """
                SELECT
                    id,
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt"
                FROM
                    "QuestionBank"
                WHERE
                    "questionSlug" = :questionSlug
                LIMIT 1
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("questionSlug", slug);
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
    public boolean updateQuestion(final QuestionBank inputQuestion) {
        String sql = """
                UPDATE "QuestionBank"
                SET
                    "questionSlug" = :slug,
                    "questionDifficulty" = :difficulty,
                    "questionNumber" = :number,
                    "questionLink" = :link,
                    "questionTitle" = :title,
                    description = :desc,
                    "acceptanceRate" = :ac
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setString("slug", inputQuestion.getQuestionSlug());
            stmt.setObject("difficulty", inputQuestion.getQuestionDifficulty().name(), java.sql.Types.OTHER);
            stmt.setInt("number", inputQuestion.getQuestionNumber());
            stmt.setString("link", inputQuestion.getQuestionLink());
            stmt.setString("title", inputQuestion.getQuestionTitle());
            stmt.setString("desc", inputQuestion.getDescription());
            stmt.setObject("ac", inputQuestion.getAcceptanceRate());
            stmt.setObject("id", UUID.fromString(inputQuestion.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update question", e);
        }
    }

    @Override
    public boolean deleteQuestionById(final String id) {
        String sql = "DELETE FROM \"QuestionBank\" WHERE id=:id";

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error while deleting question", e);
        }
    }

    @Override
    public QuestionBank getRandomQuestion() {
        QuestionBank question = null;
        String sql = """
                SELECT
                    id,
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt"
                FROM
                    "QuestionBank"
                ORDER BY RANDOM()
                LIMIT 1
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    question = mapResultSetToQuestion(rs);
                    return question;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve random question", e);
        }

        return question;
    }

    @Override
    public List<QuestionBank> getQuestionsByTopic(final LeetcodeTopicEnum topic) {
        List<QuestionBank> questions = new ArrayList<>();
        String sql = """
                SELECT DISTINCT
                    qb.id,
                    qb."questionSlug",
                    qb."questionDifficulty",
                    qb."questionNumber",
                    qb."questionLink",
                    qb."questionTitle",
                    qb.description,
                    qb."acceptanceRate",
                    qb."createdAt"
                FROM
                    "QuestionBank" qb
                INNER JOIN
                    "QuestionTopic" qt ON qb.id = qt."questionBankId"
                WHERE
                    qt.topic = :topic
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("topic", topic.getLeetcodeEnum(), java.sql.Types.OTHER);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions by topic", e);
        }

        return questions;
    }

    @Override
    public List<QuestionBank> getQuestionsByDifficulty(final QuestionDifficulty difficulty) {
        List<QuestionBank> questions = new ArrayList<>();
        String sql = """
                SELECT
                    id,
                    "questionSlug",
                    "questionDifficulty",
                    "questionNumber",
                    "questionLink",
                    "questionTitle",
                    description,
                    "acceptanceRate",
                    "createdAt"
                FROM
                    "QuestionBank"
                WHERE
                    "questionDifficulty" = :difficulty
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("difficulty", difficulty, java.sql.Types.OTHER);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve questions by difficulty", e);
        }

        return questions;
    }

    @Override
    public List<QuestionBank> getAllQuestions() {
        List<QuestionBank> questions = new ArrayList<>();
        String sql = """
                    SELECT
                        id,
                        "questionSlug",
                        "questionDifficulty",
                        "questionNumber",
                        "questionLink",
                        "questionTitle",
                        description,
                        "acceptanceRate",
                        "createdAt"
                    FROM "QuestionBank"
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    questions.add(mapResultSetToQuestion(rs));
                }
                return questions;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to retrieve all questions", e);
        }
    }
}
