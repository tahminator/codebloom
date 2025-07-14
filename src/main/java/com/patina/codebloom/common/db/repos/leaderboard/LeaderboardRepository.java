package com.patina.codebloom.common.db.repos.leaderboard;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public interface LeaderboardRepository {
    Leaderboard getRecentLeaderboardMetadata();

    Leaderboard getLeaderboardMetadataById(String id);

    ArrayList<UserWithScore> getRecentLeaderboardUsers(int page, int pageSize, String query, boolean patina);

    ArrayList<UserWithScore> getLeaderboardUsersById(String id, int page, int pageSize, String query, boolean patina);

    boolean disableLeaderboardById(String leaderboardId);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param leaderboard - required fields:
     * <ul>
     * <li>name</li>
     * </ul>
     */
    void addNewLeaderboard(Leaderboard leaderboard);

    /**
     * @note If you want to update the users in a leaderboard, use
     * updateUserFromLeaderboard instead.
     *
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param leaderboard - overridden fields:
     * <ul>
     * <li>name</li>
     * <li>createdAt</li>
     * <li>deletedAt</li>
     * </ul>
     */
    boolean updateLeaderboard(Leaderboard leaderboard);

    boolean addUserToLeaderboard(String userId, String leaderboardId);

    boolean updateUserPointsFromLeaderboard(String leaderboardId, String userId, int totalScore);

    int getRecentLeaderboardUserCount(boolean patina, String query);

    int getLeaderboardUserCountById(String id, boolean patina, String query);

    int getLeaderboardCount();

    ArrayList<Leaderboard> getAllLeaderboardsShallow(int page, int pageSize, String query);

    boolean addAllUsersToLeaderboard(String leaderboardId);
}
