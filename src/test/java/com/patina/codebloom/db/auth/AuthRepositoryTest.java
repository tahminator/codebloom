package com.patina.codebloom.db.auth;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.auth.Auth;
import com.patina.codebloom.common.db.repos.auth.AuthRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class AuthRepositoryTest {
    private AuthRepository authRepository;
    private Auth testAuth;

    @Autowired
    public AuthRepositoryTest(final AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @BeforeAll
    void createTestAuth() {
        testAuth = Auth.builder()
                        // this represents the Leetcode cookie auth token
                        .token(UUID.randomUUID().toString())
                        .build();

        authRepository.createAuth(testAuth);
    }

    @Test
    @Order(1)
    void findMostRecentAuth() {
        Auth possibleAuth = authRepository.getMostRecentAuth();

        if (possibleAuth == null) {
            fail("most recent auth not found");
        }

        if (!testAuth.equals(possibleAuth)) {
            log.info("[DEBUG] - possibleAuth: {}", possibleAuth.toString());
            log.info("[DEBUG] - testAuth: {}", testAuth.toString());
            fail("most recent auth does not equal to test auth");
        }
    }

    @Test
    void findAuthById() {
        Auth possibleAuth = authRepository.getAuthById(testAuth.getId());

        if (possibleAuth == null) {
            fail("most recent auth not found");
        }

        if (!testAuth.equals(possibleAuth)) {
            log.info("[DEBUG] - possibleAuth: {}", possibleAuth.toString());
            log.info("[DEBUG] - testAuth: {}", testAuth.toString());
            fail("most recent auth does not equal to test auth");
        }
    }

    @Test
    void updateAuth() {
        testAuth.setToken("yoooo");
        boolean isSuccessful = authRepository.updateAuth(testAuth);

        if (!isSuccessful) {
            fail("updating test auth has failed");
        }
    }

    @AfterAll
    void deleteTestAuth() {
        boolean isSuccessful = authRepository.deleteAuthById(testAuth.getId());

        if (!isSuccessful) {
            fail("failed to delete test auth");
        }
    }
}
