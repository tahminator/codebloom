package com.patina.codebloom.common.db.repos.task;

import java.util.List;

import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;

public interface BackgroundTaskRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param task - required fields:
     * <ul>
     * <li>task</li>
     * </ul>
     * optional fields:
     * <ul>
     * <li>completedAt (defaults to {@code NOW()})</li>
     * </ul>
     */
    void createBackgroundTask(BackgroundTask task);

    BackgroundTask getBackgroundTaskById(String id);

    List<BackgroundTask> getBackgroundTasksByTaskEnum(BackgroundTaskEnum taskEnum);

    BackgroundTask getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum taskEnum);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param task - overridable fields:
     * <ul>
     * <li>task</li>
     * <li>completedAt</li>
     * </ul>
     */
    boolean updateBackgroundTaskById(BackgroundTask task);

    boolean deleteBackgroundTaskById(String id);
}
