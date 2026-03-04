package org.patinanetwork.codebloom.common.db.repos.user;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.patinanetwork.codebloom.common.db.helper.NamedPreparedStatement;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserMetricsSqlRepository implements UserMetricsRepository {

    private final DataSource ds;

    public UserMetricsSqlRepository(final DataSource ds) {
        this.ds = ds;
    }

    private UserMetrics parseResultSetToUserMetrics(final ResultSet rs) throws SQLException {
        return UserMetrics.builder()
                .id(rs.getString("id"))
                .userId(rs.getString("userId"))
                .points(rs.getInt("points"))
                .createdAt(rs.getObject("createdAt", OffsetDateTime.class))
                .deletedAt(Optional.ofNullable(rs.getObject("deletedAt", OffsetDateTime.class)))
                .build();
    }

    @Override
    public void createUserMetrics(final UserMetrics userMetrics) {
        String sql = """
                INSERT INTO "UserMetrics"
                    (id, "userId", points)
                VALUES
                    (:id, :userId, :points)
                RETURNING
                    id, "createdAt"
                """;

        userMetrics.setId(UUID.randomUUID().toString());

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(userMetrics.getId()));
            stmt.setObject("userId", UUID.fromString(userMetrics.getUserId()));
            stmt.setInt("points", userMetrics.getPoints());

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userMetrics.setCreatedAt(rs.getObject("createdAt", OffsetDateTime.class));
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.createUserMetrics", e);
            throw new RuntimeException("Failed to create user metrics", e);
        }
    }

    @Override
    public Optional<UserMetrics> findUserMetricsById(final String id) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    points,
                    "createdAt",
                    "deletedAt"
                FROM
                    "UserMetrics"
                WHERE
                    id = :id
                    AND "deletedAt" IS NULL
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToUserMetrics(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.findUserMetricsById", e);
            throw new RuntimeException("Failed to fetch user metrics by id", e);
        }

        return Optional.empty();
    }

    @Override
    public List<UserMetrics> findUserMetricsByUserId(final String userId) {
        String sql = """
                SELECT
                    id,
                    "userId",
                    points,
                    "createdAt",
                    "deletedAt"
                FROM
                    "UserMetrics"
                WHERE
                    "userId" = :userId
                    AND "deletedAt" IS NULL
                """;

        List<UserMetrics> results = new ArrayList<>();

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    results.add(parseResultSetToUserMetrics(rs));
                }
            }
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.findUserMetricsByUserId", e);
            throw new RuntimeException("Failed to fetch user metrics by userId", e);
        }

        return results;
    }

    @Override
    public boolean deleteUserMetricsById(final String id) {
        String sql = """
                UPDATE "UserMetrics"
                SET
                    "deletedAt" = NOW()
                WHERE
                    id = :id
                    AND "deletedAt" IS NULL
                """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected == 1;
        } catch (SQLException e) {
            log.error("Exception thrown in UserMetricsSqlRepository.deleteUserMetricsById", e);
            throw new RuntimeException("Failed to delete user metrics by id", e);
        }
    }
}
