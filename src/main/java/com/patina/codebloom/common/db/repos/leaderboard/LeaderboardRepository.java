package com.patina.codebloom.common.db.repos.leaderboard;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public interface LeaderboardRepository {
    Leaderboard getRecentLeaderboardMetadata();

    ArrayList<UserWithScore> getRecentLeaderboardUsers(int page, int pageSize, String query, boolean patina);

    boolean disableLeaderboardById(String leaderboardId);

    Leaderboard addNewLeaderboard(Leaderboard leaderboard);

    /**
     * @implNote If you want to update the users in a leaderboard, use
     * updateUserFromLeaderboard instead.
     */
    boolean updateLeaderboard(Leaderboard leaderboard);

    boolean addUserToLeaderboard(String userId, String leaderboardId);

    boolean updateUserPointsFromLeaderboard(String leaderboardId, String userId, int totalScore);

    int getRecentLeaderboardUserCount(boolean patina, String query);

    ArrayList<Leaderboard> getAllLeaderboardsShallow();

}
