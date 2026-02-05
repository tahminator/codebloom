package org.patinanetwork.codebloom.common.utils.leaderboard;

import java.util.List;
import org.patinanetwork.codebloom.common.db.models.user.UserWithScore;
import org.patinanetwork.codebloom.common.page.Indexed;

public final class LeaderboardUtils {

    private LeaderboardUtils() {}

    public static List<UserWithScore> filterUsersWithPoints(final List<UserWithScore> users) {
        return users.stream().filter(user -> user.getTotalScore() > 0).toList();
    }

    public static List<Indexed<UserWithScore>> filterIndexedUsersWithPoints(final List<Indexed<UserWithScore>> users) {
        return users.stream()
                .filter(indexed -> indexed.getItem().getTotalScore() > 0)
                .toList();
    }
}
