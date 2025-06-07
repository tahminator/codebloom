package com.patina.codebloom.common.db.repos.weekly;

import com.patina.codebloom.common.db.models.weekly.WeeklyMessage;

public interface WeeklyMessageRepository {
    WeeklyMessage getLatestWeeklyMessage();

    boolean createLatestWeeklyMessage(WeeklyMessage message);
}
