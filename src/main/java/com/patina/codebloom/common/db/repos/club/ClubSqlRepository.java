package com.patina.codebloom.common.db.repos.club;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.club.Club;
import com.patina.codebloom.common.db.models.usertag.Tag;

@Component
public class ClubSqlRepository implements ClubRepository {
    private Connection conn;

    public ClubSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private Club parseResultSetToClub(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var name = rs.getString("name");
        var description = rs.getString("description");
        var slug = rs.getString("slug");
        var splashIconUrl = rs.getString("splashIconUrl");
        var password = rs.getString("password");
        var tagValue = rs.getString("tag");
        Tag tag = tagValue != null ? Tag.valueOf(tagValue) : null;

        return Club.builder()
                        .id(id)
                        .name(name)
                        .description(description)
                        .slug(slug)
                        .splashIconUrl(splashIconUrl)
                        .password(password)
                        .tag(tag)
                        .build();
    }
    @Override
    public void createClub(final Club club) {
        club.setId(UUID.randomUUID().toString());
        String sql = """
                        INSERT INTO "Club"
                            (id, name, description, slug, "splashIconUrl", password, tag)
                        VALUES
                            (:id, :name, :description, :slug, :splashIconUrl, :password, :tag)
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)){
            stmt.setObject("id", UUID.fromString(club.getId()));
            stmt.setString("name", club.getName());
            stmt.setString("description", club.getDescription());
            stmt.setString("slug", club.getSlug());
            stmt.setString("splashIconUrl", club.getSplashIconUrl());
            stmt.setString("password", club.getPassword());
            stmt.setObject("tag", club.getTag());
            stmt.executeUpdate();
        }catch (SQLException e) {
            throw new RuntimeException("Failed to create Club", e);
        }
    }

    @Override
    public Club updateClub(final Club club) {
        String sql = """
                    UPDATE
                        "Club"
                    SET
                        "name" = :name,
                        "description" = :description,
                        "slug" = :slug,
                        "splashIconUrl" = :splashIconUrl,
                        "password" = :password,
                        "tag" = :tag
                    WHERE
                        id = :id
                    """;
         try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)){
            stmt.setObject("id", UUID.fromString(club.getId()));
            stmt.setString("name", club.getName());
            stmt.setString("description", club.getDescription());
            stmt.setString("slug", club.getSlug());
            stmt.setString("splashIconUrl", club.getSplashIconUrl());
            stmt.setString("password", club.getPassword());
            stmt.setObject("tag", club.getTag());
            stmt.executeUpdate();
            return getClubById(club.getId());
         } catch (SQLException e) {
            throw new RuntimeException("Failed to update Club", e);
        }
        
    }

    @Override
    public Club getClubById(final String id) {
            String sql = """
                    SELECT
                        id,
                        "name",
                        "description",
                        "slug",
                        "splashIconUrl",
                        "password",
                        "tag"
                    FROM
                        "Club"
                    WHERE
                        id = :id
                    """;
            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
                stmt.setObject("id", UUID.fromString(id));
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return parseResultSetToClub(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get Club by id", e);
            }
            return null;
    }

    @Override
    public Club getClubBySlug(final String slug) {
            String sql = """
                    SELECT
                        id,
                        "name",
                        "description",
                        "slug",
                        "splashIconUrl",
                        "password",
                        "tag"
                    FROM
                        "Club"
                    WHERE
                        "slug" = :slug
                    """;
            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
                stmt.setString("slug", slug);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return parseResultSetToClub(rs);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to get Club by slug", e);
            }
            return null;
    }

    @Override
    public boolean deleteClubBySlug(final String slug) {
            String sql = """
                DELETE FROM "Club"
                WHERE "slug" = :slug
                """;

            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
                 stmt.setString("slug", slug);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Failed to delete club by slug", e);
    }
    }

    @Override
    public boolean deleteClubById(final String id) {
            String sql = """
                DELETE FROM "Club"
                WHERE id = :id
                """;

            try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
                stmt.setObject("id", UUID.fromString(id));
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
    } catch (SQLException e) {
        throw new RuntimeException("Failed to delete club by id", e);
    }
        
    }


}
