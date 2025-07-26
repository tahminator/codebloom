package com.patina.codebloom.common.security;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.PrivateUser;

public class AuthenticationObject {
    private PrivateUser user;
    private Session session;

    public AuthenticationObject(final PrivateUser user, final Session session) {
        this.user = user;
        this.session = session;
    }

    public PrivateUser getUser() {
        return user;
    }

    public Session getSession() {
        return session;
    }
}
