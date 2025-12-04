package com.patina.codebloom.config;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.session.SessionRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * Use this if you need to override the admin behavior.
 *
 * <p>The returned user is an admin user, while the session attached to this user lasts until Jan 01, 2099, 12:59:50 PM
 * EST
 *
 * @see <a href=
 *     "https://github.com/tahminator/codebloom/tree/main/src/test/java/com/patina/codebloom/admin/AdminControllerTest.java">Example
 *     on how to use this test config</a>
 */
@TestConfiguration
public class TestProtector {

    private final UserRepository userRepository;
    private final SessionRepository sesssionRepository;

    public TestProtector(final UserRepository userRepository, final SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sesssionRepository = sessionRepository;
    }

    @Bean
    public Protector protector() {
        return new Protector(sesssionRepository, userRepository) {
            @Override
            public AuthenticationObject validateSession(final HttpServletRequest request) {
                User mockAdminUser = userRepository.getUserById("ed3bfe18-e42a-467f-b4fa-07e8da4d2555");
                Session mockAdminSession = sesssionRepository.getSessionById("d99e10a2-6285-46f0-8150-ba4727b520f4");
                return new AuthenticationObject(mockAdminUser, mockAdminSession);
            }

            // User is an admin, so just send the same thing.
            @Override
            public AuthenticationObject validateAdminSession(final HttpServletRequest request) {
                return validateSession(request);
            }
        };
    }
}
