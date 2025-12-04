package com.patina.codebloom;

import static org.junit.jupiter.api.Assertions.assertTrue;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.config.TestJobNotifyListener;
import com.patina.codebloom.utilities.ServerMetadataObject;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestJobNotifyListener.class)
public class ApiControllerTest {

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

    @Test
    void testBaseApiRoute() {
        ApiResponder<ServerMetadataObject> apiResponder = RestAssured.given()
                .when()
                .get("/api")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<ApiResponder<ServerMetadataObject>>() {});

        assertTrue(apiResponder.isSuccess(), "Testing apiResponder success is true");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");
        ServerMetadataObject serverMetadataObject = apiResponder.getPayload();
        assertTrue(serverMetadataObject != null, "Testing server metadata object is not null");
        assertTrue(serverMetadataObject.getVersion() != null, "Testing server version is not null");
        assertTrue(serverMetadataObject.getName() != null, "Testing server name is not null");
        List<String> authors = serverMetadataObject.getAuthors();
        assertTrue(authors != null, "Testing authors list is not null");
        assertTrue(authors.size() == 6, "Testing length of authors, expected 6.");
    }
}
