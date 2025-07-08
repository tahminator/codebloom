package com.patina.codebloom.common.db.repos.session;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.Session;

public interface SessionRepository {
    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     *
     * @param agent - required fields:
     * <ul>
     * <li>userId</li>
     * <li>expiresAt</li>
     * </ul>
     */
    void createSession(Session session);

    Session getSessionById(String id);

    ArrayList<Session> getSessionsByUserId(String userId);

    boolean deleteSessionById(String id);
}
