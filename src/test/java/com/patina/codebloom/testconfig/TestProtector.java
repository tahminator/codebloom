package com.patina.codebloom.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.patina.codebloom.common.db.models.Session;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.user.UserRepository;
import com.patina.codebloom.common.security.AuthenticationObject;
import com.patina.codebloom.common.security.Protector;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Use this if you need to override the admin behavior.
 *
 * NOTE: THE USER EXISTS AND IS REAL, BUT THE SESSION IS NOT!
 *
 * TODO - Create an infinitely long session to use for this case.
 *
 * @see <a href=
 * "https://github.com/tahminator/codebloom/tree/main/src/test/java/com/patina/codebloom/admin/AdminControllerTest.java">Example
 * on how to use this test config</a>
 */
@TestConfiguration
public class TestProtector {
    private UserRepository userRepository;

    public TestProtector(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public Protector protector() {
        return new Protector(null, userRepository) {
            @Override
            public AuthenticationObject validateSession(HttpServletRequest request) {
                User mockAdminUser = userRepository.getUserById("ed3bfe18-e42a-467f-b4fa-07e8da4d2555");
                Session fakeSession = new Session("ed3bfe18-e42a-467f-b4fa-07e8da4d2555", StandardizedLocalDateTime.now().plusYears(10L));
                return new AuthenticationObject(mockAdminUser, fakeSession);
            }

            // User is an admin, so just send the same thing.
            @Override
            public AuthenticationObject validateAdminSession(HttpServletRequest request) {
                return validateSession(request);
            }
        };
    }
}
