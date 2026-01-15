package org.patinanetwork.codebloom.common.security;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.patinanetwork.codebloom.common.db.models.Session;
import org.patinanetwork.codebloom.common.db.models.user.User;

@Hidden // https://codebloom.notion.site/fix-Protected-on-swagger-2a47c85563aa8003ac50e8bd0241ba37?source=copy_link
@AllArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
public class AuthenticationObject {

    private User user;
    private Session session;
}
