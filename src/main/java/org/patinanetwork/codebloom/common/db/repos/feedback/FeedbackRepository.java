package org.patinanetwork.codebloom.common.db.repos.feedback;

import java.util.Optional;
import org.patinanetwork.codebloom.common.db.models.feedback.Feedback;

public interface FeedbackRepository {
    /**
     * Finds a feedback by its ID.
     *
     * @param id the feedback ID
     * @return an {@code Optional} containing the feedback if found, or {@code Optional.empty()} if not found
     */
    Optional<Feedback> findFeedbackById(String id);

    /**
     * Creates a new feedback in the database.
     *
     * @note - The provided object's id and createdAt fields will be automatically set by the database.
     * @param feedback - required fields:
     *     <ul>
     *       <li>title
     *       <li>description
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>email (defaults to empty Optional)
     *     </ul>
     */
    void createFeedback(Feedback feedback);

    /**
     * Deletes a feedback by its ID.
     *
     * @param id the feedback ID
     * @return {@code true} if the feedback was deleted, {@code false} if not found
     */
    boolean deleteFeedbackById(String id);

    /**
     * Updates an existing feedback in the database.
     *
     * @param feedback - required fields:
     *     <ul>
     *       <li>id (must exist in database)
     *       <li>title
     *       <li>description
     *     </ul>
     *     optional fields:
     *     <ul>
     *       <li>email
     *     </ul>
     *
     * @return {@code true} if the feedback was updated, {@code false} if not found
     */
    boolean updateFeedback(Feedback feedback);
}
