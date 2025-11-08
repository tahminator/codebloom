package com.patina.codebloom.utilities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.config.NoJdaRequired;
import com.patina.codebloom.config.TestProtector;

import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestProtector.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RateLimitingFilterTest extends NoJdaRequired {
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
    void testApiRateLimitingFilterTest() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(21);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch rateLimitSetup = new CountDownLatch(20);
        CountDownLatch done = new CountDownLatch(21);

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    start.await();

                    RestAssured.given()
                                    .when()
                                    .header("Content-Type", "application/json")
                                    .get("/api")
                                    .then()
                                    .statusCode(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    rateLimitSetup.countDown();
                    done.countDown();
                }
            });
        }

        executor.submit(() -> {
            try {
                rateLimitSetup.await();

                var apiResponder = RestAssured.given()
                                .when()
                                // Everyone should have this user ID on their dev db from the repeated
                                // migration.
                                .header("Content-Type", "application/json")
                                .get("/api")
                                .then()
                                .statusCode(429)
                                .extract()
                                .as(new TypeRef<ApiResponder<ServerMetadataObject>>() {
                                });

                assertFalse(apiResponder.isSuccess());
            } catch (Exception e) {
                e.printStackTrace();
                fail("Expected last request to be rate limited");
            } finally {
                done.countDown();
            }
        });

        start.countDown();
        done.await(8, TimeUnit.SECONDS);

        executor.shutdown();
    }
}
