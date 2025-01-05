package com.patina.codebloom.common.db.repos.session;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.Session;

public interface SessionRepository {
    Session createSession(Session session);

    Session getSessionById(String id);

    ArrayList<Session> getSessionsByUserId(String userId);

    boolean deleteSessionById(String id);
}
