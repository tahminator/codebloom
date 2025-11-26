package com.patina.codebloom.common.security;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Hidden // https://codebloom.notion.site/fix-Protected-on-swagger-2a47c85563aa8003ac50e8bd0241ba37?source=copy_link
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class AuthenticationObject {

    private User user;
    private Session session;
}
