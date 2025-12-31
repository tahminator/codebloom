package com.patina.codebloom.common.db.repos.job;

import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class JobSqlRepository implements JobRepository {

    private DataSource ds;

    public JobSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private Job parseResultSetToJob(final ResultSet resultSet) throws SQLException {
        return Job.builder()
                .id(resultSet.getString("id"))
                .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                .processedAt(resultSet.getObject("processedAt", OffsetDateTime.class))
                .completedAt(resultSet.getObject("completedAt", OffsetDateTime.class))
                .nextAttemptAt(resultSet.getObject("nextAttemptAt", OffsetDateTime.class))
                .status(JobStatus.valueOf(resultSet.getString("status")))
                .questionId(resultSet.getString("questionId"))
                .attempts(resultSet.getInt("attempts"))
                .build();
    }

    @Override
    public void createJob(final Job job) {
        String sql = """
            INSERT INTO "Job"
                (id, "questionId", status)
            VALUES
                (?, ?, ?)
            RETURNING
                "createdAt", "nextAttemptAt"
            """;

        job.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(job.getId()));
            stmt.setString(2, job.getQuestionId());
            stmt.setObject(3, job.getStatus().name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    job.setCreatedAt(rs.getObject("createdAt", OffsetDateTime.class));
                    job.setNextAttemptAt(rs.getObject("nextAttemptAt", OffsetDateTime.class));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create job", e);
        }
    }

    @Override
    public Job findJobById(final String id) {
        String sql = """
            SELECT
                id,
                "createdAt",
                "processedAt",
                "completedAt",
                "nextAttemptAt",
                status,
                "questionId",
                "attempts"
            FROM
                "Job"
            WHERE
                id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToJob(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find job by id", e);
        }

        return null;
    }

    @Override
    public List<Job> findIncompleteJobs(final int maxJobs) {
        List<Job> result = new ArrayList<>();
        String sql = """
            SELECT
                id,
                "createdAt",
                "processedAt",
                "completedAt",
                "nextAttemptAt",
                status,
                "questionId",
                "attempts"
            FROM
                "Job"
            WHERE
                status = ?
                AND "nextAttemptAt" <= NOW()
            ORDER BY
                "nextAttemptAt" ASC
            LIMIT ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, JobStatus.INCOMPLETE.name(), java.sql.Types.OTHER);
            stmt.setInt(2, maxJobs);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToJob(rs));
                }
            }

            return result;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find incomplete jobs", e);
        }
    }

    @Override
    public boolean updateJob(final Job job) {
        String sql = """
            UPDATE "Job"
            SET
                "processedAt" = ?,
                "completedAt" = ?,
                "nextAttemptAt" = ?,
                status = ?,
                "attempts" = ?
            WHERE
                id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, job.getProcessedAt());
            stmt.setObject(2, job.getCompletedAt());
            stmt.setObject(3, job.getNextAttemptAt());
            stmt.setObject(4, job.getStatus().name(), java.sql.Types.OTHER);
            stmt.setObject(5, job.getAttempts());
            stmt.setObject(6, UUID.fromString(job.getId()));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update job", e);
        }
    }

    @Override
    public boolean deleteJobById(final String id) {
        String sql = """
            DELETE FROM "Job"
            WHERE id = ?
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete job", e);
        }
    }

    @Override
    public boolean deleteAllJobs() {
        String sql = """
            DELETE FROM "Job"
            """;

        try (Connection conn = ds.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete job", e);
        }
    }
}
