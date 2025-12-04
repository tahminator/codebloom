package com.patina.codebloom.common.db.repos.discord.club.metadata;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class DiscordClubMetadataSqlRepository implements DiscordClubMetadataRepository {

    private Connection conn;

    public DiscordClubMetadataSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private DiscordClubMetadata parseResultSetToDiscordClubMetadata(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var guildId = Optional.ofNullable(rs.getString("guildId"));
        var leaderboardChannelId = Optional.ofNullable(rs.getString("leaderboardChannelId"));
        var discordClubId = rs.getString("discordClubId");

        return DiscordClubMetadata.builder()
                .id(id)
                .guildId(guildId)
                .leaderboardChannelId(leaderboardChannelId)
                .discordClubId(discordClubId)
                .build();
    }

    @Override
    public void createDiscordClubMetadata(final DiscordClubMetadata discordClubMetadata) {
        discordClubMetadata.setId(UUID.randomUUID().toString());
        String sql = """
            INSERT INTO "DiscordClubMetadata"
                (id, "guildId", "leaderboardChannelId", "discordClubId")
            VALUES
                (:id, :guildId, :leaderboardChannelId, :discordClubId)
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClubMetadata.getId()));
            stmt.setString("guildId", discordClubMetadata.getGuildId().orElse(null));
            stmt.setString(
                    "leaderboardChannelId",
                    discordClubMetadata.getLeaderboardChannelId().orElse(null));
            stmt.setObject("discordClubId", UUID.fromString(discordClubMetadata.getDiscordClubId()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DiscordClubMetadata", e);
        }
    }

    @Override
    public Optional<DiscordClubMetadata> getDiscordClubMetadataById(final String id) {
        String sql = """
            SELECT
                id,
                "guildId",
                "leaderboardChannelId",
                "discordClubId"
            FROM
                "DiscordClubMetadata"
            WHERE
                id = :id
            """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToDiscordClubMetadata(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get DiscordClubMetadata by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateDiscordClubMetadata(final DiscordClubMetadata discordClubMetadata) {
        String sql = """
            UPDATE
                "DiscordClubMetadata"
            SET
                "guildId" = :guildId,
                "leaderboardChannelId" = :leaderboardChannelId,
                "discordClubId" = :discordClubId
            WHERE
                id = :id
            """;

        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClubMetadata.getId()));
            stmt.setString("guildId", discordClubMetadata.getGuildId().orElse(null));
            stmt.setString(
                    "leaderboardChannelId",
                    discordClubMetadata.getLeaderboardChannelId().orElse(null));
            stmt.setObject("discordClubId", UUID.fromString(discordClubMetadata.getDiscordClubId()));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update DiscordClubMetadata", e);
        }
    }

    @Override
    public boolean deleteDiscordClubMetadataById(final String id) {
        String sql = """
            DELETE FROM "DiscordClubMetadata"
            WHERE id = :id
            """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete DiscordClubMetadata by id", e);
        }
    }

    @Override
    public Optional<DiscordClubMetadata> getDiscordClubMetadataByClubId(final String id) {
        String sql = """
            SELECT
                id,
                "guildId",
                "leaderboardChannelId",
                "discordClubId"
            FROM
                "DiscordClubMetadata"
            WHERE
                "discordClubId" = :id
            """;
        try (NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetToDiscordClubMetadata(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get metadata by club id", e);
        }
        return Optional.empty();
    }
}
