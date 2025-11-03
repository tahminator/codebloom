package com.patina.codebloom.common.db.repos.leaderboard;

import java.util.List;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.page.Indexed;

public interface LeaderboardRepository {
    Leaderboard getRecentLeaderboardMetadata();

    Leaderboard getLeaderboardMetadataById(String id);

    @Deprecated
    List<UserWithScore> getRecentLeaderboardUsers(LeaderboardFilterOptions options);

    List<UserWithScore> getLeaderboardUsersById(String id, LeaderboardFilterOptions options);

    /**
     * Returns an ordered list of {@code UserWithScore} wrapped in {@code Indexed},
     * ranked by their global position on the specified leaderboard.
     *
     * <p>
     * This internally calls {@code getLeaderboardUsersById}, but the result is
     * globally ranked—filter options are ignored for index calculation.
     * </p>
     *
     * @implNote Filter options are passed to {@code getLeaderboardUsersById} only
     * to fetch the users, not to determine their global rank.
     */
    List<Indexed<UserWithScore>> getGlobalRankedIndexedLeaderboardUsersById(String leaderboardId, LeaderboardFilterOptions options);

    /**
     * Returns an ordered list of {@code UserWithScore} wrapped in {@code Indexed},
     * ranked by their position on the specified leaderboard using the provided
     * filter options.
     *
     * <p>
     * This internally calls {@code getLeaderboardUsersById} and ranks the users
     * according to the filtered context.
     * </p>
     *
     * @implNote All filter options are used to calculate index, excluding search.
     */
    List<Indexed<UserWithScore>> getRankedIndexedLeaderboardUsersById(String leaderboardId, LeaderboardFilterOptions options);

    /**
     * Returns a specific user's global rank on the leaderboard wrapped in
     * {@code Indexed}. The rank is calculated globally without applying any
     * filters.
     *
     * @param leaderboardId The ID of the leaderboard
     * @param userId The ID of the user to fetch
     * @return The user with their global rank, or null if user not found
     */
    Indexed<UserWithScore> getGlobalRankedUserById(String leaderboardId, String userId);

    /**
     * Returns a specific user's filtered rank on the leaderboard wrapped in
     * {@code Indexed}. The rank is calculated with the provided filter options
     * applied.
     *
     * @param leaderboardId The ID of the leaderboard
     * @param userId The ID of the user to fetch
     * @param options The filter options to apply when calculating rank
     * @return The user with their filtered rank, or null if user not found
     */
    Indexed<UserWithScore> getFilteredRankedUserById(String leaderboardId, String userId, LeaderboardFilterOptions options);

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

    @Deprecated
    int getRecentLeaderboardUserCount(LeaderboardFilterOptions options);

    int getLeaderboardUserCountById(String id, LeaderboardFilterOptions options);

    int getLeaderboardCount();

    List<Leaderboard> getAllLeaderboardsShallow(LeaderboardFilterOptions options);

    boolean addAllUsersToLeaderboard(String leaderboardId);
}
