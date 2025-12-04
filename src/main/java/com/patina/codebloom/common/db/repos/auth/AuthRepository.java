package com.patina.codebloom.common.db.repos.auth;

import com.patina.codebloom.common.db.models.auth.Auth;

public interface AuthRepository {
    /**
     * NOTE - Modifies the passed in Auth object and overrides any new properties from the database.
     *
     * @param auth - required fields:
     *     <ul>
     *       <li>token
     *       <li>csrf
     *     </ul>
     */
    void createAuth(Auth auth);

    /**
     * NOTE - Modifies the passed in Auth object and overrides any new properties from the database.
     *
     * @param auth - overridden fields:
     *     <ul>
     *       <li>token
     *       <li>csrf
     *     </ul>
     */
    boolean updateAuthById(Auth auth);

    Auth getAuthById(String id);

    Auth getMostRecentAuth();

    boolean deleteAuthById(String id);
}
