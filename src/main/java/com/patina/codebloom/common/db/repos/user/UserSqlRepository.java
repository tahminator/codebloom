package com.patina.codebloom.common.db.repos.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.DbConnection;
import com.patina.codebloom.common.db.models.user.PrivateUser;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;

@Component
public class UserSqlRepository implements UserRepository {
    private Connection conn;
    private final UserTagRepository userTagRepository;

    public UserSqlRepository(final DbConnection dbConnection, final UserTagRepository userTagRepository) {
        this.conn = dbConnection.getConn();
        this.userTagRepository = userTagRepository;
    }

    /**
     * @implNote - You can not set tags on a new user. Create the user, and if the
     * returned user is not null, you can use updateUserTagById from
     * {@link UserTagRepository}
     */
    @Override
    public User createNewUser(final User user) {
        String sql = "INSERT INTO \"User\" (id, \"discordName\", \"discordId\", \"leetcodeUsername\", \"nickname\") VALUES (?, ?, ?, ?, ?)";
        user.setId(UUID.randomUUID().toString());
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(user.getId()));
            stmt.setString(2, user.getDiscordName());
            stmt.setString(3, user.getDiscordId());
            // User cannot be instantiated with a leetcodeUsername, it gets collected after
            // user authentication.
            stmt.setNull(4, java.sql.Types.VARCHAR);
            stmt.setString(5, user.getNickname());

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
    public User getUserById(final String inputId) {
        User user = null;
        String sql = "SELECT id, \"discordId\", \"discordName\", \"leetcodeUsername\", \"nickname\", admin FROM \"User\" WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");

                    var tags = userTagRepository.findTagsByUserId(id);

                    user = new User(id, discordId, discordName, leetcodeUsername, nickname, admin, tags);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return user;
    }

    @Override
    public User getUserByDiscordId(final String inputDiscordId) {
        User user = null;
        String sql = "SELECT id, \"discordId\", \"discordName\", \"leetcodeUsername\", \"nickname\", admin FROM \"User\" WHERE \"discordId\"=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputDiscordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");

                    var tags = userTagRepository.findTagsByUserId(id);

                    user = new User(id, discordId, discordName, leetcodeUsername, nickname, admin, tags);
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

        try (PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user count", e);
        }

        return 0;
    }

    @Override
    public User updateUser(final User inputUser) {
        String sql = "UPDATE \"User\" SET \"discordName\"=?, \"discordId\"=?, \"leetcodeUsername\"=?, \"nickname\"=?, \"admin\"=? WHERE id=?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, inputUser.getDiscordName());
            stmt.setString(2, inputUser.getDiscordId());
            stmt.setString(3, inputUser.getLeetcodeUsername());
            stmt.setString(4, inputUser.getNickname());
            stmt.setBoolean(5, inputUser.getAdmin());
            stmt.setObject(6, UUID.fromString(inputUser.getId()));

            // We don't care what this actually returns, it can never be more than 1 anyways
            // because id is UNIQUE. Just return the new user every time if we want to do
            // any work on it.
            stmt.executeUpdate();

            return getUserByDiscordId(inputUser.getDiscordId());
        } catch (SQLException e) {
            throw new RuntimeException("Error while updating user", e);
        }
    }

    @Override
    public ArrayList<User> getAllUsers() {
        ArrayList<User> users = new ArrayList<>();
        String sql = "SELECT id, \"discordId\", \"discordName\", \"leetcodeUsername\", \"nickname\", \"admin\" FROM \"User\"";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");

                    var tags = userTagRepository.findTagsByUserId(id);

                    users.add(new User(id, discordId, discordName, leetcodeUsername, nickname, admin, tags));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return users;
    }

    @Override
    public PrivateUser getPrivateUserById(final String inputId) {
        PrivateUser user = null;
        String sql = """
                        SELECT
                            id,
                            "discordId",
                            "discordName",
                            "leetcodeUsername",
                            "nickname",
                            "admin",
                            "verifyKey"
                        FROM "User"
                        WHERE id = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(inputId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");
                    var verifyKey = rs.getString("verifyKey");

                    var tags = userTagRepository.findTagsByUserId(id);

                    user = new PrivateUser(id, discordId, discordName, leetcodeUsername, nickname, admin, verifyKey, tags);
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user", e);
        }

        return user;
    }

    @Override
    public boolean userExistsByLeetcodeUsername(final String leetcodeUsername) {
        String sql = """
                        SELECT
                            1
                        FROM "User"
                        WHERE "leetcodeUsername" = ?
                        LIMIT 1
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, leetcodeUsername);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while retrieving user count with given leetcodeUsername", e);
        }
    }

    @Override
    public UserWithScore getUserWithScoreById(final String userId, final String leaderboardId) {
        String sql = """
                            SELECT
                                u.id,
                                u."discordId",
                                u."discordName",
                                u."leetcodeUsername",
                                u.nickname,
                                u.admin,
                                m."totalScore"
                            FROM
                                "User" u
                            JOIN "Metadata" m ON m."userId" = u.id
                            WHERE
                                u.id = ?
                                AND
                                m."leaderboardId" = ?
                        """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setObject(1, UUID.fromString(userId));
            stmt.setObject(2, UUID.fromString(leaderboardId));
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    var id = rs.getString("id");
                    var discordId = rs.getString("discordId");
                    var discordName = rs.getString("discordName");
                    var leetcodeUsername = rs.getString("leetcodeUsername");
                    var nickname = rs.getString("nickname");
                    var admin = rs.getBoolean("admin");
                    var totalScore = rs.getInt("totalScore");

                    var tags = userTagRepository.findTagsByUserId(id);

                    var userWithScore = new UserWithScore(id, discordId, discordName, leetcodeUsername, nickname, admin, totalScore, tags);
                    System.out.println(userWithScore.getDiscordId());
                    return userWithScore;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get user with score by id", e);
        }

        return null;
    }

}
