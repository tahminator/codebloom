package com.patina.codebloom.common.db.repos.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.User;

@Component
public class UserSqlRepository implements UserRepository {
    DbConnection dbConnection;
    Connection conn;

    public UserSqlRepository(DbConnection dbConnection) {
        this.dbConnection = dbConnection;
        this.conn = dbConnection.getConn();
    }

    @Override
    public User createNewUser(User user) {
        String sql = "INSERT INTO \"User\" (id, \"discordName\", \"discordId\", \"leetcodeUsername\") VALUES (?, ?, ?, ?)";
        user.setId(UUID.randomUUID().toString());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(user.getId()));
            stmt.setString(2, user.getDiscordName());
            stmt.setString(3, user.getDiscordId());
            // User cannot be instantiated with a leetcodeUsername, it gets collected after
            // user authentication.
            stmt.setNull(4, java.sql.Types.VARCHAR);

            // We don't care what this actually returns, it can never be more than 1 anyways
            // because id is UNIQUE. Just return the new user every time if we want to do
            // any work on it.
            stmt.executeUpdate();

            return getUserById(user.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Error while creating user", e);
        }
    }

    @Override
    public User getUserById(String inputId) {
        User user = null;
        String sql = "SELECT id, \"discordId\", \"discordName\", \"leetcodeUsername\" FROM \"User\" WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    user = new User(id, discordId, discordName, leetcodeUsername);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return user;
    }

    @Override
    public User getUserByDiscordId(String inputDiscordId) {
        User user = null;
        String sql = "SELECT id, \"discordId\", \"discordName\", \"leetcodeUsername\" FROM \"User\" WHERE \"discordId\"=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputDiscordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    user = new User(id, discordId, discordName, leetcodeUsername);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return user;
    }

    @Override
    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM \"User\"";

        try (PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user count", e);
        }

        return 0;
    }

    @Override
    public User updateUserById(User inputUser) {
        String sql = "UPDATE \"User\" SET \"discordName\"=?, \"discordId\"=?, \"leetcodeUsername\"=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputUser.getDiscordName());
            stmt.setString(2, inputUser.getDiscordId());
            stmt.setString(3, inputUser.getLeetcodeUsername());
            stmt.setObject(4, UUID.fromString(inputUser.getId()));

            // We don't care what this actually returns, it can never be more than 1 anyways
            // because id is UNIQUE. Just return the new user every time if we want to do
            // any work on it.
            stmt.executeUpdate();

            return getUserByDiscordId(inputUser.getDiscordId());
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user", e);
        }
    }
}
