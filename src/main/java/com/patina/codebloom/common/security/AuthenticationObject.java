package com.patina.codebloom.common.security;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AuthenticationObject {
    private final User user;
    private final Session session;
}
