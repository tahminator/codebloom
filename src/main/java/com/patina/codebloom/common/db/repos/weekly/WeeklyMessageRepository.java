package com.patina.codebloom.common.db.repos.weekly;

import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;

public interface WeeklyMessageRepository {
    WeeklyMessage getLatestWeeklyMessage();

    WeeklyMessage getWeeklyMessageById(String id);

    /**
     * @note - The provided object's methods will be overriden with any returned
     * data from the database.
     *
     *
     * @param message - required fields:
     * <ul>
     * </ul>
     *
     */
    boolean createLatestWeeklyMessage(WeeklyMessage message);

    /**
     * If you have no purpose for the newly created object, just use this.
     *
     * @note - The provided object's methods will be overriden with any returned
     * data from the database.
     *
     *
     * @param message - required fields:
     * <ul>
     * </ul>
     *
     */
    boolean createLatestWeeklyMessage();

    boolean deleteWeeklyMessageById(String id);

    boolean deleteLatestWeeklyMessage();
}
