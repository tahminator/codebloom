package com.patina.codebloom.website.leetcode.repo;

import com.patina.codebloom.website.leetcode.model.Auth;

public interface AuthRepository {
    Auth createAuth(Auth auth);

    boolean updateAuth(Auth auth);

    Auth getAuthById(String id);

    Auth getMostRecentAuth();
}
