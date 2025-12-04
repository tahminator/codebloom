package com.patina.codebloom.common.db.repos.session;

import com.patina.codebloom.common.db.models.Session;
import java.util.ArrayList;

public interface SessionRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned data from the database.
     * @param session - required fields:
     *     <ul>
     *       <li>userId
     *       <li>expiresAt
     *     </ul>
     */
    void createSession(Session session);

    Session getSessionById(String id);

    ArrayList<Session> getSessionsByUserId(String userId);

    boolean deleteSessionById(String id);
}
