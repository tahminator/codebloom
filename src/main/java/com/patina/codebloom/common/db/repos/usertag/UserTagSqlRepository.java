package com.patina.codebloom.common.db.repos.usertag;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;
import com.patina.codebloom.common.db.repos.usertag.options.UserTagFilterOptions;

@Component
public class UserTagSqlRepository implements UserTagRepository {
    private Connection conn;

    public UserTagSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private UserTag parseResultSetToTag(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var createdAt = rs.getTimestamp("createdAt").toLocalDateTime();
        var userId = rs.getString("userId");
        var tag = Tag.valueOf(rs.getString("tag"));
        return new UserTag(id, createdAt, userId, tag);
    }

    @Override
    public UserTag findTagByTagId(final String tagId) {
        String sql = """
                        SELECT
                            id,
                            "createdAt",
                            "userId",
                            tag
                        FROM
                            "UserTag"
                        WHERE
                            id = :id
                                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(tagId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToTag(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by tag ID", e);
        }

        return null;
    }

    @Override
    public UserTag findTagByUserIdAndTag(final String userId, final Tag tag) {
        String sql = """
                        SELECT
                            id,
                            "createdAt",
                            "userId",
                            tag
                        FROM
                            "UserTag"
                        WHERE
                            tag = :tag
                            AND
                            "userId" = :userId
                                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("tag", tag.name(), java.sql.Types.OTHER);
            stmt.setObject("userId", UUID.fromString(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetToTag(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by user ID and tag", e);
        }

        return null;
    }

    @Override
    public ArrayList<UserTag> findTagsByUserId(final String userId) {
        ArrayList<UserTag> tags = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "createdAt",
                            "userId",
                            tag
                        FROM
                            "UserTag"
                        WHERE
                            "userId" = :userId
                                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var tag = parseResultSetToTag(rs);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tag by user ID and tag", e);
        }

        return tags;
    }

    @Override
    public ArrayList<UserTag> findTagsByUserId(final String userId, final UserTagFilterOptions options) {
        ArrayList<UserTag> tags = new ArrayList<>();
        String sql = """
                        SELECT
                            id,
                            "createdAt",
                            "userId",
                            tag
                        FROM
                            "UserTag"
                        WHERE
                            "userId" = :userId
                        AND
                            (cast(:pointOfTime AS timestamptz) IS NULL OR "createdAt" <= :pointOfTime)
                                """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));
            if (options.getPointOfTime() == null) {
                stmt.setNull("pointOfTime", Types.TIMESTAMP_WITH_TIMEZONE);
            } else {
                stmt.setObject("pointOfTime", options.getPointOfTime());
            }
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var tag = parseResultSetToTag(rs);
                    if (tag != null) {
                        tags.add(tag);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user tags by user ID with filter options", e);
        }

        return tags;
    }

    @Override
    public void createTag(final UserTag userTag) {
        userTag.setId(UUID.randomUUID().toString());
        String sql = """
                            INSERT INTO "UserTag"
                                (id, "userId", tag)
                            VALUES
                                (:id, :userId, :tag)
                            RETURNING
                                "createdAt"
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(userTag.getId()));
            stmt.setObject("userId", UUID.fromString(userTag.getUserId()));
            stmt.setObject("tag", userTag.getTag().name(), java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    userTag.setCreatedAt(rs.getTimestamp("createdAt").toLocalDateTime());
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to create user tag by user ID", e);
        }

    }

    @Override
    public boolean deleteTagByTagId(final String tagId) {
        String sql = """
                        DELETE FROM
                            "UserTag"
                        WHERE
                            id = :id
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(tagId));
            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by tag ID", e);
        }

    }

    @Override
    public boolean deleteTagByUserIdAndTag(final String userId, final Tag tag) {
        String sql = """
                            DELETE FROM
                                "UserTag"
                            WHERE
                                "userId" = :userId
                                AND
                                tag = :tag
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("userId", UUID.fromString(userId));
            stmt.setObject("tag", tag.name(), java.sql.Types.OTHER);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by user ID and tag", e);
        }
    }

}
