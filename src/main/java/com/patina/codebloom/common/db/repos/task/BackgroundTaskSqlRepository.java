package com.patina.codebloom.common.db.repos.task;

import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class BackgroundTaskSqlRepository implements BackgroundTaskRepository {

    private final DataSource ds;

    public BackgroundTaskSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private BackgroundTask parseResultSetToBackgroundTask(final ResultSet rs) throws SQLException {
        return BackgroundTask.builder()
                .id(rs.getString("id"))
                .completedAt(StandardizedOffsetDateTime.normalize(rs.getObject("completedAt", OffsetDateTime.class)))
                .task(BackgroundTaskEnum.valueOf(rs.getString("task")))
                .build();
    }

    @Override
    public void createBackgroundTask(final BackgroundTask task) {
        String sql = """
                INSERT INTO "BackgroundTask"
                    (id, task, "completedAt")
                VALUES
                    (:id, :task, :completedAt)
            """;

        task.setId(UUID.randomUUID().toString());
        if (task.getCompletedAt() == null) {
            task.setCompletedAt(StandardizedOffsetDateTime.now());
        }
        task.setCompletedAt(StandardizedOffsetDateTime.normalize(task.getCompletedAt()));
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(task.getId()));
            stmt.setObject("task", task.getTask().name(), java.sql.Types.OTHER);
            stmt.setObject("completedAt", task.getCompletedAt());
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected != 1) {
                throw new RuntimeException("Failed to create background task: Rows affected != 1");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to create background task", e);
        }
    }

    @Override
    public BackgroundTask getBackgroundTaskById(final String id) {
        String sql = """
                SELECT
                    *
                FROM
                    "BackgroundTask"
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToBackgroundTask(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get background task by ID", e);
        }

        return null;
    }

    @Override
    public List<BackgroundTask> getBackgroundTasksByTaskEnum(final BackgroundTaskEnum taskEnum) {
        List<BackgroundTask> result = new ArrayList<>();
        String sql = """
                SELECT
                    *
                FROM
                    "BackgroundTask"
                WHERE
                    task = :task
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("task", taskEnum.name(), java.sql.Types.OTHER);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetToBackgroundTask(rs));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get background tasks by task enum", e);
        }

        return result;
    }

    @Override
    public BackgroundTask getMostRecentlyCompletedBackgroundTaskByTaskEnum(final BackgroundTaskEnum taskEnum) {
        String sql = """
                SELECT
                    *
                FROM
                    "BackgroundTask"
                WHERE
                    task = :task
                ORDER BY
                    "completedAt" DESC
                LIMIT 1
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("task", taskEnum.name(), java.sql.Types.OTHER);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToBackgroundTask(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to get most recently completed background task by task enum", e);
        }

        return null;
    }

    @Override
    public boolean updateBackgroundTaskById(final BackgroundTask task) {
        String sql = """
                        UPDATE "BackgroundTask"
                        SET
                            task = :task,
                            "completedAt" = :completedAt
                        WHERE
                            id = :id
            """;

        task.setCompletedAt(StandardizedOffsetDateTime.normalize(task.getCompletedAt()));
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("task", task.getTask().name(), java.sql.Types.OTHER);
            stmt.setObject("completedAt", task.getCompletedAt());
            stmt.setObject("id", UUID.fromString(task.getId()));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to update background task by ID", e);
        }
    }

    @Override
    public boolean deleteBackgroundTaskById(final String id) {
        String sql = """
                DELETE FROM
                    "BackgroundTask"
                WHERE
                    id = :id
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete background task by id", e);
        }
    }
}
