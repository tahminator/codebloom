package com.patina.codebloom.common.security;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;

public class AuthenticationObject {
    private User user;
    private Session session;

    public AuthenticationObject(final User user, final Session session) {
        this.user = user;
        this.session = session;
    }

    public User getUser() {
        return user;
    }

    public Session getSession() {
        return session;
    }
}
