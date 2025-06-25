package com.patina.codebloom.common.db.repos.auth;

import com.patina.codebloom.common.db.models.auth.Auth;

public interface AuthRepository {
    /**
     * NOTE - Modifies the passed in Auth object and overrides any new properties
     * from the database.
     */
    void createAuth(Auth auth);

    boolean updateAuth(Auth auth);

    Auth getAuthById(String id);

    Auth getMostRecentAuth();

    boolean deleteAuthById(String id);
}
