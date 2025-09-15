package com.patina.codebloom.announcement;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.patina.codebloom.api.admin.body.CreateAnnouncementBody;
import com.patina.codebloom.common.db.models.announcement.Announcement;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.ZoneId;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.config.TestProtector;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(TestProtector.class)
@Slf4j
public class AnnouncementControllerTest {
    @LocalServerPort
    private int port;

    private CreateAnnouncementBody createAnnouncementBody = CreateAnnouncementBody
                    .builder()
                    .message("Hi this is a test message!")
                    .showTimer(true)
                    .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                    .build();
    private Announcement testAnnouncement;

    /**
     * only used for cleanup after all tests are done.
     */
    @Autowired
    private AnnouncementRepository announcementRepository;

    @BeforeEach
    void setUpPort() {
        RestAssured.port = port;
    }

    @BeforeAll
    static void setUpUri() {
        RestAssured.baseURI = "http://localhost";
    }

    @AfterAll
    void deleteTestUrl() {
        if (testAnnouncement == null) {
            return;
        }

        announcementRepository.deleteAnnouncementById(testAnnouncement.getId());
    }

    /**
     * Use builder pattern to pass in input.
     * 
     * @throws JsonProcessingException
     */
    private String builtTestCreateAnnouncementPayload(final CreateAnnouncementBody createAnnouncementBody) throws JsonProcessingException {
        Map<String, Object> body = new HashMap<>();
        body.put("message", createAnnouncementBody.getMessage());
        body.put("showTimer", createAnnouncementBody.isShowTimer());
        body.put("expiresAt", createAnnouncementBody.getExpiresAt().toString());
        return new ObjectMapper().writeValueAsString(body);
    }

    @Test
    @Order(1)
    void createNewTestAnnouncementAsAdmin() throws JsonProcessingException {
        ApiResponder<Announcement> apiResponder = RestAssured
                        .given()
                        .when()
                        .header("Content-Type", "application/json")
                        .body(builtTestCreateAnnouncementPayload(createAnnouncementBody))
                        .post("/api/admin/announcement/create")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<ApiResponder<Announcement>>() {
                        });

        assertTrue(apiResponder != null, "Expected apiResponder to not be equal to null");
        assertTrue(apiResponder.isSuccess() == true, "Testing apiResponder success is true");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");

        testAnnouncement = apiResponder.getPayload();

        assertTrue(testAnnouncement != null, "Expected announcement to not be equal to null");
        assertTrue(testAnnouncement.getExpiresAt().atZone(ZoneId.systemDefault()).toInstant().truncatedTo(ChronoUnit.SECONDS)
                        .equals(createAnnouncementBody.getExpiresAt().truncatedTo(ChronoUnit.SECONDS)),
                        "Expected announcement response and announcement request body expiresAt to be equal");
        assertTrue(testAnnouncement.getMessage().equals(createAnnouncementBody.getMessage()),
                        "Expected announcement response and announcement request body message to be equal");
        assertTrue(testAnnouncement.isShowTimer() == createAnnouncementBody.isShowTimer(),
                        "Expected announcement response and announcement request body showTimer to be equal");
    }

    @Test
    @Order(2)
    void getTestAnnouncement() {
        ApiResponder<Announcement> apiResponder = RestAssured
                        .given()
                        .when()
                        .get("/api/announcement")
                        .then()
                        .statusCode(200)
                        .extract()
                        .as(new TypeRef<ApiResponder<Announcement>>() {
                        });

        assertTrue(apiResponder != null, "Expected apiResponder to not be equal to null");
        assertTrue(apiResponder.isSuccess() == true, "Testing apiResponder success is true");
        assertTrue(apiResponder.getMessage() != null, "Testing apiResponder message is not null");
        Announcement newlyFetchedAnnouncement = apiResponder.getPayload();

        assertTrue(newlyFetchedAnnouncement != null, "expected newlyFetchedAnnouncement to not be null");

        log.info("testAnnouncement: {}", testAnnouncement.toString());
        log.info("possibleTestAnnouncement: {}", newlyFetchedAnnouncement.toString());

        assertTrue(testAnnouncement.equals(newlyFetchedAnnouncement),
                        "expected the previously created test announcement to be equal to the newly fetched announcement");
    }
}
