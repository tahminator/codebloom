package com.patina.codebloom.common.db.repos.user.v2;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;

public interface UserV2Repository {
    User getUserByLeetcodeUsername(String leetcodeUsername);

    UserWithScore getUserWithScoreByLeetcodeUsername(String userLeetcodeUsername, String leaderboardId);
}
