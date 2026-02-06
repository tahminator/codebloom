package org.patinanetwork.codebloom.common.utils.leaderboard;

import java.util.List;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;

public final class LeaderboardUtils {

    public static List<UserWithScore> filterUsersWithPoints(final List<UserWithScore> users) {
        return users.stream().filter(user -> user.getTotalScore() > 0).toList();
    }
}
