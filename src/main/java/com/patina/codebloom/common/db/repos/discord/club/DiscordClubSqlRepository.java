package com.patina.codebloom.common.db.repos.discord.club;

import com.patina.codebloom.common.db.helper.NamedPreparedStatement;
import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.club.metadata.DiscordClubMetadataSqlRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.sql.DataSource;
import org.springframework.stereotype.Component;

@Component
public class DiscordClubSqlRepository implements DiscordClubRepository {

    private final DiscordClubMetadataSqlRepository discordClubMetadataSqlRepository;

    private DataSource ds;

    public DiscordClubSqlRepository(
            final DataSource ds, final DiscordClubMetadataSqlRepository discordClubMetadataSqlRepository) {
        this.ds = ds;
        this.discordClubMetadataSqlRepository = discordClubMetadataSqlRepository;
    }

    private DiscordClub parseResultSetTDiscordClub(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var name = rs.getString("name");
        var description = Optional.ofNullable(rs.getString("description"));
        var createdAt = StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class));
        var deletedAt = Optional.ofNullable(rs.getObject("deletedAt", OffsetDateTime.class))
                .map(StandardizedOffsetDateTime::normalize);
        Tag tag = Tag.valueOf(rs.getString("tag"));
        var metadata = discordClubMetadataSqlRepository.getDiscordClubMetadataByClubId(id);

        return DiscordClub.builder()
                .id(id)
                .name(name)
                .description(description)
                .tag(tag)
                .discordClubMetadata(metadata)
                .createdAt(createdAt)
                .deletedAt(deletedAt)
                .build();
    }

    @Override
    public void createDiscordClub(final DiscordClub discordClub) {
        discordClub.setId(UUID.randomUUID().toString());
        String sql = """
            INSERT INTO "DiscordClub"
                (id, name, description, tag, "deletedAt")
            VALUES
                (:id, :name, :description, :tag, :deletedAt)
            RETURNING
                "createdAt"
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClub.getId()));
            stmt.setString("name", discordClub.getName());
            stmt.setString("description", discordClub.getDescription().orElse(null));
            stmt.setObject("tag", discordClub.getTag().name(), java.sql.Types.OTHER);
            stmt.setObject("deletedAt", discordClub.getDeletedAt().orElse(null));

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    discordClub.setCreatedAt(
                            StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class)));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DiscordClub", e);
        }
    }

    @Override
    public Optional<DiscordClub> getDiscordClubById(final String id) {
        String sql = """
            SELECT
                *
            FROM
                "DiscordClub"
            WHERE
                id = :id
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(parseResultSetTDiscordClub(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get DiscordClub by id", e);
        }
        return Optional.empty();
    }

    @Override
    public boolean updateDiscordClubById(final DiscordClub discordClub) {
        String sql = """
            UPDATE
                "DiscordClub"
            SET
                "name" = :name,
                "description" = :description,
                "tag" = :tag,
                "deletedAt" = :deletedAt
            WHERE
                id = :id
            """;
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(discordClub.getId()));
            stmt.setString("name", discordClub.getName());
            stmt.setString("description", discordClub.getDescription().orElse(null));
            stmt.setObject("tag", discordClub.getTag().name(), java.sql.Types.OTHER);
            stmt.setObject("deletedAt", discordClub.getDeletedAt().orElse(null));

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected == 1;
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
        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            stmt.setObject("id", UUID.fromString(id));
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete DiscordClub by id", e);
        }
    }

    @Override
    public List<DiscordClub> getAllActiveDiscordClubs() {
        List<DiscordClub> result = new ArrayList<>();

        String sql = """
            SELECT
                *
            FROM
                "DiscordClub"
            WHERE
                "deletedAt" IS NULL
            """;

        try (Connection conn = ds.getConnection();
                NamedPreparedStatement stmt = new NamedPreparedStatement(conn, sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    result.add(parseResultSetTDiscordClub(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get all active Discord clubs", e);
        }
        return result;
    }
}
