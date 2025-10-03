package com.patina.codebloom.common.security;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class AuthenticationObject {
    private User user;
    private Session session;
}
