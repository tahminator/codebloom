package com.patina.codebloom.website.auth.repo;

import java.util.ArrayList;

import com.patina.codebloom.website.auth.model.Session;

public interface SessionRepository {
    Session createSession(Session session);

    Session getSessionById(String id);

    ArrayList<Session> getSessionsByUserId(String userId);

    boolean deleteSessionById(String id);
}
