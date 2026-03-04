package org.patinanetwork.codebloom.common.db.repos.user;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;

public interface UserMetricsRepository {

    /**
     * Creates a new user metrics record in the database.
     *
     * @note - The provided object's id and createdAt fields will be set from the database.
     * @param userMetrics - required fields:
     *     <ul>
     *       <li>userId
     *       <li>points
     *     </ul>
     */
    void createUserMetrics(UserMetrics userMetrics);

    /**
     * Finds a user metrics record by its ID, excluding soft-deleted records.
     *
     * @param id the user metrics ID
     * @return an {@code Optional} containing the record if found and not deleted, or {@code Optional.empty()} otherwise
     */
    Optional<UserMetrics> findUserMetricsById(String id);

    /**
     * Finds all user metrics records for a given user, excluding soft-deleted records.
     *
     * @param userId the user ID
     * @return a list of user metrics records for the user
     */
    List<UserMetrics> findUserMetricsByUserId(String userId);

    /**
     * Soft-deletes a user metrics record by setting its deletedAt timestamp.
     *
     * @param id the user metrics ID
     * @return {@code true} if the record was deleted, {@code false} if not found or already deleted
     */
    boolean deleteUserMetricsById(String id);
}
