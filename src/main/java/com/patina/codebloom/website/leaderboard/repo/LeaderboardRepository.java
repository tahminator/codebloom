package com.patina.codebloom.website.leaderboard.repo;

import java.util.ArrayList;

import com.patina.codebloom.website.leaderboard.model.Leaderboard;
import com.patina.codebloom.website.leaderboard.model.LeaderboardWithUsers;
import com.patina.codebloom.website.leaderboard.model.UserWithScore;

public interface LeaderboardRepository {
    ArrayList<Leaderboard> getAllLeaderboardsShallow();

    ArrayList<LeaderboardWithUsers> getAllLeaderboardsFull();

    LeaderboardWithUsers getRecentLeaderboardShallow();

    LeaderboardWithUsers getRecentLeaderboardFull();

    LeaderboardWithUsers getLeaderboardByIdShallow(String leaderboardId);

    LeaderboardWithUsers getLeaderboardByIdFull(String leaderboardId);

    boolean disableLeaderboardById(String leaderboardId);

    Leaderboard addNewLeaderboard(Leaderboard leaderboard);

    /**
     * @implNote If you want to update the users in a
     * leaderboard, use updateUserFromLeaderboard instead.
     */
    boolean updateLeaderboard(Leaderboard leaderboard);

    boolean addUserToLeaderboard(String userId, String leaderboardId);

    UserWithScore getUserFromLeaderboard(String leaderboardId, String userId);

    boolean updateUserPointsFromLeaderboard(String leaderboardId, String userId, int totalScore);

}
