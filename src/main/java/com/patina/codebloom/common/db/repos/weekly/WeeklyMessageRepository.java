package com.patina.codebloom.common.db.repos.weekly;

import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;

public interface WeeklyMessageRepository {
    WeeklyMessage getLatestWeeklyMessage();

    boolean createLatestWeeklyMessage(WeeklyMessage message);

    /**
     * If you have no purpose for the newly created object, just use this.
     */
    boolean createLatestWeeklyMessage();
}
