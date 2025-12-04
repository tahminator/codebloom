package com.patina.codebloom.common.db.repos.task;

import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;
import java.util.List;

public interface BackgroundTaskRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param task - required fields:
     *     <ul>
     *       <li>task
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>completedAt (defaults to {@code NOW()})
     *     </ul>
     */
    void createBackgroundTask(BackgroundTask task);

    BackgroundTask getBackgroundTaskById(String id);

    List<BackgroundTask> getBackgroundTasksByTaskEnum(BackgroundTaskEnum taskEnum);

    BackgroundTask getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum taskEnum);

    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param task - overridable fields:
     *     <ul>
     *       <li>task
     *       <li>completedAt
     *     </ul>
     */
    boolean updateBackgroundTaskById(BackgroundTask task);

    boolean deleteBackgroundTaskById(String id);
}
