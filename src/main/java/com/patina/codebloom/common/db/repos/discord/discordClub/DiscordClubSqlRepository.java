package com.patina.codebloom.common.db.repos.discord.discordClub;

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
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

@Component
public class DiscordClubSqlRepository implements DiscordClubRepository {

    private Connection conn;

    public DiscordClubSqlRepository(final DbConnection dbConnection) {
        this.conn = dbConnection.getConn();
    }

    private DiscordClub mapRowTDiscordClub(final ResultSet rs) throws SQLException {
        var id = rs.getString("id");
        var name = rs.getString("name");
        var description = rs.getString("description");
        var tagValue = rs.getString("tag");
        var createdAt = StandardizedOffsetDateTime.normalize(rs.getObject("createdAt", OffsetDateTime.class));
        Tag tag = tagValue != null ? Tag.valueOf(tagValue) : null;

        return DiscordClub.builder()
                        .id(id)
                        .name(name)
                        .description(description)
                        .tag(tag)
                        .createdAt(createdAt)
                        .build();
    }

    @Override
    public DiscordClub createDiscordClub(final DiscordClub discordClub) {
        discordClub.setId(UUID.randomUUID().toString());
        String sql = """
                        INSERT INTO "DiscordClub"
                            (id, name, description, tag, createdAt)
                        VALUES
                            (:id, :name, :description, :tag, :createdAt)
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
            stmt.executeUpdate();
            return discordClub;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to create DiscordClub", e);
        }
    }

    @Override
    public DiscordClub getDiscordClubById(final String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDiscordClubById'");
    }

    @Override
    public void updateDiscordClub(final DiscordClub discordClub) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateDiscordClub'");
    }

    @Override
    public void deleteDiscordClub(final DiscordClub discordClub) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteDiscordClub'");
    }

}
