package com.patina.codebloom.website.auth.client;

import com.patina.codebloom.website.auth.model.Session;
import com.patina.codebloom.website.auth.model.User;

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
