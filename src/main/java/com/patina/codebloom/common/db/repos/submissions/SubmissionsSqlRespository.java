package com.patina.codebloom.common.db.repos.submissions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.Submission;

@Component
public class SubmissionsSqlRespository implements SubmissionsRepository {
    DbConnection dbConnection;
    Connection conn;

    public SubmissionsSqlRespository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public List<Submission> findSubmissionsByUsername(String leetcodeUsername) {
        String sql = """
                    SELECT id, "questionSlug", "timestamp", "statusDisplay", "lang"
                    FROM "Submissions"
                    WHERE "leetcodeUsername" = ?
                """;

        List<Submission> results = new ArrayList<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, leetcodeUsername);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Submission submission = new Submission();
                submission.setId(rs.getString("id"));
                submission.setQuestionSlug(rs.getString("questionSlug"));
                submission.setLang(rs.getString("lang"));
                submission.setStatusDisplay(rs.getString("statusDisplay"));
                submission.setTimestamp(rs.getString("timestamp"));

                results.add(submission);
            }

            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find submissions by username", e);
        }

    }

    @Override
    public List<Submission> findQuestionBySlug(String questionSlug) {
        // TO DO! HELP! :D
        throw new UnsupportedOperationException("Unimplemented method 'findQuestionBySlug'");
    }

    @Override
    public void insertSubmission(Submission submission) {
        String sql = """
                    INSERT INTO \"Submissions\" (id, \"leetcodeUsername\", \"questionSlug\", \"timestamp\", \"statusDisplay\", \"lang\")
                    VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, submission.getId());
            stmt.setString(2, submission.getLeetcodeUsername());
            stmt.setString(3, submission.getQuestionSlug());
            stmt.setString(4, submission.getTimestamp());
            stmt.setString(5, submission.getStatusDisplay());
            stmt.setString(6, submission.getLang());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert submission into DB", e);
        }
    }

}
