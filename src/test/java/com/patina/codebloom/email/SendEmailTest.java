package com.patina.codebloom.email;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import com.patina.codebloom.config.NoJdaRequired;
import com.patina.codebloom.config.TestProtector;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestProtector.class)
public class SendEmailTest extends NoJdaRequired {
    @LocalServerPort
    private int port;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void setUpUri() {
        RestAssured.baseURI = "http://localhost";
    }

    private String buildTestEmailPayload(final String email) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("email", email);
        return new ObjectMapper().writeValueAsString(body);
    }

    @Test
    void testNonValidSchoolEmail() throws JsonProcessingException {
        String payload = buildTestEmailPayload("name@example.com ");
        ApiResponder<Empty> apiResponder = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .body(payload)
                        .post("/api/auth/school/enroll")
                        .then()
                        .statusCode(400)
                        .extract()
                        .as(new TypeRef<ApiResponder<Empty>>() {
                        });

        assertTrue(apiResponder != null, "Expected apiResponder to not be equal to null");
        assertTrue(apiResponder.isSuccess() == false, "Testing apiResponder success is false");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");
    }

    @Test
    void testValidSchoolEmail() throws JsonProcessingException {
        String payload = buildTestEmailPayload("TIMMY.APPLES420@myhunter.cuny.edu");

        ApiResponder<Empty> apiResponder = RestAssured.given()
                        .header("Content-Type", "application/json")
                        .body(payload)
                        .post("/api/auth/school/enroll")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<ApiResponder<Empty>>() {
                        });

        assertTrue(apiResponder != null, "Expected apiResponder to not be equal to null");
        assertTrue(apiResponder.isSuccess() == true, "Testing apiResponder success is true");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");
    }
}
