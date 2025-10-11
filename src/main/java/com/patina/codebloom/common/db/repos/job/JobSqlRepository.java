package com.patina.codebloom.common.db.repos.job;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.job.Job;
import com.patina.codebloom.common.db.models.job.JobStatus;

@Component
public class JobSqlRepository implements JobRepository {
    private Connection conn;

    public JobSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Job parseResultSetToJob(final ResultSet resultSet) throws SQLException {
        return Job.builder()
                        .id(resultSet.getString("id"))
                        .createdAt(resultSet.getObject("createdAt", OffsetDateTime.class))
                        .processedAt(resultSet.getObject("processedAt", OffsetDateTime.class))
                        .completedAt(resultSet.getObject("completedAt", OffsetDateTime.class))
                        .status(JobStatus.valueOf(resultSet.getString("status")))
                        .questionId(resultSet.getString("questionId"))
                        .build();
    }

    @Override
    public Job createJob(final Job job) {
        String sql = """
                        INSERT INTO "Job"
                            (id, "questionId", status)
                        VALUES
                            (?, ?, ?)
                        RETURNING
                            id, "createdAt"
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            UUID jobId = UUID.randomUUID();
            stmt.setObject(1, jobId);
            stmt.setString(2, job.getQuestionId());
            stmt.setString(3, job.getStatus().name());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    job.setId(rs.getString("id"));
                    job.setCreatedAt(rs.getObject("createdAt", OffsetDateTime.class));
                    return job;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create job", e);
        }

        throw new RuntimeException("Failed to create job - no result returned");
    }

    @Override
    public Job findJobById(final String id) {
        String sql = """
                        SELECT
                            id,
                            "createdAt",
                            "processedAt",
                            "completedAt",
                            status,
                            "questionId"
                        FROM
                            "Job"
                        WHERE
                            id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
                            status,
                            "questionId"
                        FROM
                            "Job"
                        WHERE
                            status = ?
                        ORDER BY
                            "createdAt" ASC
                        LIMIT ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, JobStatus.INCOMPLETE.name());
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
                            status = ?
                        WHERE
                            id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, job.getProcessedAt());
            stmt.setObject(2, job.getCompletedAt());
            stmt.setString(3, job.getStatus().name());
            stmt.setObject(4, UUID.fromString(job.getId()));

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

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete job", e);
        }
    }
}