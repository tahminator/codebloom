package com.patina.codebloom.common.db.repos.auth;

import com.patina.codebloom.common.db.models.auth.Auth;

public interface AuthRepository {
    Auth createAuth(Auth auth);

    boolean updateAuth(Auth auth);

    Auth getAuthById(String id);

    Auth getMostRecentAuth();
}
