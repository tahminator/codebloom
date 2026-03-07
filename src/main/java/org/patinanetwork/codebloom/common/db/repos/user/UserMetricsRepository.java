package org.patinanetwork.codebloom.common.db.repos.user;

import java.util.List;
import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;
import org.patinanetwork.codebloom.common.db.repos.user.options.UserMetricsFilterOptions;

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
     * Finds user metrics records for a given user, excluding soft-deleted records.
     *
     * <p>Supports optional date range filtering and pagination via {@link UserMetricsFilterOptions}. When
     * {@code pageSize} is 0, all matching records are returned without pagination.
     *
     * @param userId the user ID
     * @param options filter and pagination options
     * @return a list of user metrics records for the user
     */
    List<UserMetrics> findUserMetrics(String userId, UserMetricsFilterOptions options);

    /**
     * Counts user metrics records for a given user, excluding soft-deleted records.
     *
     * <p>Supports optional date range filtering via {@link UserMetricsFilterOptions}.
     *
     * @param userId the user ID
     * @param options filter options (pagination fields are ignored)
     * @return the total number of matching records
     */
    int countUserMetrics(String userId, UserMetricsFilterOptions options);

    /**
     * Soft-deletes a user metrics record by setting its deletedAt timestamp.
     *
     * @param id the user metrics ID
     * @return {@code true} if the record was deleted, {@code false} if not found or already deleted
     */
    boolean deleteUserMetricsById(String id);
}
