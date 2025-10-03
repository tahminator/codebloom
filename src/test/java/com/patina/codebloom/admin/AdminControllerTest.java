package com.patina.codebloom.admin;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.user.UserDto;
import com.patina.codebloom.config.TestProtector;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestProtector.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AdminControllerTest {
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

    private String buildTestAdminToggleBody(final String id, final boolean toggleTo) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("id", id);
        body.put("toggleTo", toggleTo);

        return new ObjectMapper().writeValueAsString(body);
    }

    @Test
    void testAdminToggle() throws JsonProcessingException {
        ApiResponder<UserDto> apiResponder = RestAssured
                        .given()
                        .when()
                        // Everyone should have this user ID on their dev db from the repeated
                        // migration.
                        .header("Content-Type", "application/json")
                        .body(buildTestAdminToggleBody("e0b45c9a-9c8f-4a39-9373-39cf2a5f8055", false))
                        .post("/api/admin/user/admin/toggle")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<ApiResponder<UserDto>>() {
                        });

        assertTrue(apiResponder != null, "Expected apiResponder to not be equal to null");
        assertTrue(apiResponder.isSuccess(), "Testing apiResponder success is true");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");
        UserDto user = apiResponder.getPayload();
        assertTrue(user != null, "Expected user to not be equal to null");
        assertTrue(!user.isAdmin(), "Expected user to not be admin");
    }
}
