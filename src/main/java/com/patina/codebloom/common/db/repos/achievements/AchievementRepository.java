package com.patina.codebloom.common.db.repos.achievements;

import java.util.List;

import com.patina.codebloom.common.db.models.achievements.Achievement;

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
     * 
     * @return true if the update was successful, false otherwise
     */
    boolean updateAchievementById(Achievement achievement);
    
    /**
     * Soft deletes an achievement by setting the deletedAt timestamp.
     * 
     * @param id - the achievement ID
     * @return true if the delete was successful, false otherwise
     */
    boolean deleteAchievementById(String id);
    
    /**
     * Retrieves an achievement by its ID.
     * 
     * @param id - the achievement ID
     * @return the Achievement object if found, null otherwise
     */
    Achievement getAchievementById(String id);
    
    /**
     * Retrieves all achievements for a specific user.
     * 
     * @param userId - the user ID
     * @return list of achievements for the user, empty list if none found
     */
    List<Achievement> getAchievementsByUserId(String userId);
}