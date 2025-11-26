package com.patina.codebloom.common.db.repos.achievements;

import com.patina.codebloom.common.db.models.achievements.Achievement;
import java.util.List;

public interface AchievementRepository {
    /**
     * Creates a new achievement in the database.
     *
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param achievement - required fields:
     * <ul>
     * <li>userId</li>
     * <li>iconUrl</li>
     * <li>title</li>
     * <li>description</li>
     * <li>isActive</li>
     * </ul>
     */
    void createAchievement(Achievement achievement);
    /**
     * Updates an existing achievement by its ID.
     *
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param achievement - overridden fields:
     * <ul>
     * <li>iconUrl</li>
     * <li>title</li>
     * <li>description</li>
     * <li>isActive</li>
     * <li>deletedAt</li>
     * </ul>
     * @return updated achievement if successful
     * */
    Achievement updateAchievement(Achievement achievement);

    boolean deleteAchievementById(String id);

    Achievement getAchievementById(String id);

    List<Achievement> getAchievementsByUserId(String userId);
}
