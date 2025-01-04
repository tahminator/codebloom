package com.patina.codebloom.api.auth.infra.session;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.Session;

public interface SessionRepository {
    Session createSession(Session session);

    Session getSessionById(String id);

    ArrayList<Session> getSessionsByUserId(String userId);

    Boolean deleteSessionById(String id);
}
