package com.patina.codebloom.common.db.repos.discord.club;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.club.metadata.DiscordClubMetadataSqlRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

@Component
public class DiscordClubSqlRepository implements DiscordClubRepository {

    private final DiscordClubMetadataSqlRepository discordClubMetadataSqlRepository;

    private Connection conn;

    public DiscordClubSqlRepository(final DbConnection dbConnection, final DiscordClubMetadataSqlRepository discordClubMetadataSqlRepository) {
        this.conn = dbConnection.getConn();
        this.discordClubMetadataSqlRepository = discordClubMetadataSqlRepository;
    }

    private DiscordClub parseResultSetTDiscordClub(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var name = rs.getString("name");
        var description = rs.getString("description");
        var tagValue = rs.getString("tag");
        var createdAt = StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class));
        Tag tag = tagValue != null ? Tag.valueOf(tagValue) : null;
        var metadata = discordClubMetadataSqlRepository.getMetadataByClubId(id);

        return DiscordClub.builder()
                        .id(id)
                        .name(name)
                        .description(description)
                        .tag(tag)
                        .discordClubMetadata(metadata)
                        .createdAt(createdAt)
                        .build();
    }

    @Override
    public void createDiscordClub(final DiscordClub discordClub) {
        discordClub.setId(UUID.randomUUID().toString());
        String sql = """
                        INSERT INTO "DiscordClub"
                            (id, name, description, tag)
                        VALUES
                            (:id, :name, :description, :tag)
                        RETURNING
                            "createdAt"
                        """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClub.getId()));
            stmt.setString("name", discordClub.getName());
            stmt.setString("description", discordClub.getDescription());
            stmt.setObject("tag", discordClub.getTag() != null ? discordClub.getTag().name() : null, java.sql.Types.OTHER);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    discordClub.setCreatedAt(StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DiscordClub", e);
        }
    }

    @Override
    public DiscordClub getDiscordClubById(final String id) {
        String sql = """
                        SELECT
                            id,
                            "name",
                            "description",
                            "tag",
                            "createdAt"
                        FROM
                            "DiscordClub"
                        WHERE
                            id = :id
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return parseResultSetTDiscordClub(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get DiscordClub by id", e);
        }
        return null;
    }

    @Override
    public DiscordClub updateDiscordClub(final DiscordClub discordClub) {
        String sql = """
                        UPDATE
                            "DiscordClub"
                        SET
                            "name" = :name,
                            "description" = :description,
                            "tag" = :tag
                        WHERE
                            id = :id
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClub.getId()));
            stmt.setString("name", discordClub.getName());
            stmt.setString("description", discordClub.getDescription());
            stmt.setObject("tag", discordClub.getTag() != null ? discordClub.getTag().name() : null, java.sql.Types.OTHER);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                return getDiscordClubById(discordClub.getId());
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update DiscordClub", e);
        }
    }

    @Override
    public boolean deleteDiscordClubById(final String id) {
        String sql = """
                        DELETE FROM "DiscordClub"
                        WHERE id = :id
                        """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete DiscordClub by id", e);
        }
    }

}
