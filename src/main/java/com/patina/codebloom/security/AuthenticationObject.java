package com.patina.codebloom.security;

import com.patina.codebloom.models.Session;
import com.patina.codebloom.models.user.User;

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
