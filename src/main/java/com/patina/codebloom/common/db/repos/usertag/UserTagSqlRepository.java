package com.patina.codebloom.common.db.repos.usertag;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;

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
                            createdAt,
                            "userId",
                            tag
                        FROM
                            "UserTag"
                        WHERE
                            id = ?
                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(tagId));
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
                            tag = ?
                            AND
                            "userId" = ?
                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, tag.name(), java.sql.Types.OTHER);
            stmt.setObject(2, UUID.fromString(userId));
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
                            "userId" = ?
                                """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
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
    public void createTag(UserTag userTag) {
        userTag.setId(UUID.randomUUID().toString());
        String sql = """
                            INSERT INTO "UserTag"
                                (id, "userId", tag)
                            VALUES
                                (?, ?, ?)
                            RETURNING
                                "createdAt"
                        """;
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userTag.getId()));
            stmt.setObject(2, UUID.fromString(userTag.getUserId()));
            stmt.setObject(3, userTag.getTag().name(), java.sql.Types.OTHER);

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
                            id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(tagId));
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
                                "userId" = ?
                                AND
                                tag = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
            stmt.setObject(2, tag.name(), java.sql.Types.OTHER);

            int rowsAffected = stmt.executeUpdate();

            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete tag by user ID and tag", e);
        }
    }

}
