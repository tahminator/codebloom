package com.patina.codebloom.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.patina.codebloom.common.jwt.JWTClient;
import com.patina.codebloom.config.NoJdaRequired;

@SpringBootTest
public class JWTTest extends NoJdaRequired {
    private final JWTClient jwtClient;

    @Autowired
    public JWTTest(final JWTClient jwtClient) {
        this.jwtClient = jwtClient;
    }

    private JWTTestObject createTestableObject() {
        JWTTestObject object = new JWTTestObject();
        object.setEmail("test@test.com");
        object.setRole("USER");
        object.setUserId(UUID.randomUUID().toString());
        return object;
    }

    @Test
    void testValidCaseNotExpire() {
        JWTTestObject userTag = createTestableObject();
        String jwt = null;
        try {
            jwt = jwtClient.encode(userTag);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to create JWT");
        }
        assertNotNull(jwt, "JWT is null when it should not be.");

        JWTTestObject reParsedJsonTag = null;
        try {
            reParsedJsonTag = jwtClient.decode(jwt, JWTTestObject.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to parse JWT");
        }

        assertEquals(userTag, reParsedJsonTag);
    }

    @Test
    void testValidCaseExpire() {
        JWTTestObject userTag = createTestableObject();
        String jwt = null;
        try {
            jwt = jwtClient.encode(userTag, Duration.ofMillis(500));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            fail("Failed to create JWT");
        }
        assertNotNull(jwt, "JWT is null when it should not be.");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            fail("Sleep interrupted");
        }

        try {
            jwtClient.decode(jwt, JWTTestObject.class);
            fail("Expected TokenExpiredException was not thrown");
        } catch (JWTVerificationException e) {
            // Expected exception.
            return;
        } catch (Exception e) {
            fail("Unexpected exception thrown: " + e.getClass().getName());
        }
    }
}
