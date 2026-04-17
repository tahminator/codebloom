package org.patinanetwork.codebloom.common.db.repos.task;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTask;
import org.patinanetwork.codebloom.common.db.models.task.BackgroundTaskEnum;

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

    Optional<BackgroundTask> getBackgroundTaskById(String id);

    List<BackgroundTask> getBackgroundTasksByTaskEnum(BackgroundTaskEnum taskEnum);

    Optional<BackgroundTask> getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum taskEnum);

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
