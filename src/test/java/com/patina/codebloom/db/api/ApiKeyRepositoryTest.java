package com.patina.codebloom.db.api;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.api.ApiKey;
import com.patina.codebloom.common.db.repos.api.ApiKeyRepository;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class ApiKeyRepositoryTest extends BaseRepositoryTest {
    private final ApiKeyRepository apiKeyRepository;
    private ApiKey testApiKey;
    private ApiKey deletableApiKey;
    private final String mockedUpdatedBy = "742d5c7d-4fe2-44c7-b36a-0f5a6e2efb79";

    @Autowired
    public ApiKeyRepositoryTest(final ApiKeyRepository apiKeyRepository) {
        this.apiKeyRepository = apiKeyRepository;
    }

    @BeforeAll
    void createTestApiKey() {
        UUID.fromString(mockedUpdatedBy);

        ApiKey tempApiKey = ApiKey.builder()
                        .id(UUID.randomUUID().toString())
                        .apiKey(UUID.randomUUID().toString())
                        .expiresAt(StandardizedLocalDateTime.now().plusMinutes(5L))
                        .updatedBy(mockedUpdatedBy)
                        .build();

        apiKeyRepository.createApiKey(tempApiKey);

        testApiKey = apiKeyRepository.getApiKeyById(tempApiKey.getId());

        if (testApiKey == null) {
            fail("Failed to create and retrieve test ApiKey");
        }
    }

    @AfterAll
    void deleteTestApiKey() {
        boolean isSuccessful = apiKeyRepository.deleteApiKeyById(testApiKey.getId());

        if (!isSuccessful) {
            fail("Failed to delete test apiKey");
        }
    }

    /**
     * @note this could possibly fail via race condition if you create a new apiKey
     * in the few seconds while this test suite is running, overriding the test
     * apiKey. If you do, let the test suite finish and let `@AfterAll` delete the
     * test apiKey and then just run the test suite again.
     *
     * We run this test first just to make sure that we don't run into that issue.
     */
    @Test
    @Order(1)
    void findApiKeyById() {
        ApiKey possibleTestApiKey = apiKeyRepository.getApiKeyById(testApiKey.getId());

        if (possibleTestApiKey == null) {
            fail("most recent apiKey doesn't exist, even though we created a test apiKey");
        }

        /**
         * @note - see method note
         */
        if (!testApiKey.equals(possibleTestApiKey)) {
            fail("the test apiKey does not match the possible apiKey by Id");
        }
    }

    @Test
    @Order(2)
    void findApiKeyByHash() {
        ApiKey possibleTestApiKey = apiKeyRepository.getApiKeyByHash(testApiKey.getApiKey());

        if (possibleTestApiKey == null) {
            fail("most recent apiKey doesn't exist, even though we created a test apiKey");
        }

        /**
         * @note - see method note
         */
        if (!testApiKey.equals(possibleTestApiKey)) {
            fail("the generated test apiKey does not match the apiKey fetched with get apiKey by hash");
        }
    }

    @Test
    @Order(3)
    void findAllApiKeys() {
        List<ApiKey> apiKeysList = apiKeyRepository.getAllApiKeys();

        assertNotNull(apiKeysList);

        if (apiKeysList.isEmpty()) {
            fail("expected at least the test apiKey");
        }

        boolean found = apiKeysList.stream().anyMatch(k -> testApiKey.getId().equals(k.getId()));
        if (!found) {
            fail("test apiKey cannot be found in the list of all apiKeys");
        }
    }

    @Test
    @Order(4)
    void updateApiKeyByIdTest() {
        ApiKey updatedApiKey = ApiKey.builder()
                        .id(testApiKey.getId())
                        .apiKey(testApiKey.getApiKey())
                        .expiresAt(testApiKey.getExpiresAt())
                        .updatedBy(testApiKey.getUpdatedBy())
                        .build();

        boolean result = apiKeyRepository.updateApiKeyById(updatedApiKey);

        if (!result) {
            fail("failure to update apiKeyById");
        }

        ApiKey resultApiKey = apiKeyRepository.getApiKeyById(testApiKey.getId());

        assertNotNull(resultApiKey);
        assertEquals(resultApiKey.getId(), testApiKey.getId());
        assertEquals(resultApiKey.getUpdatedBy(), mockedUpdatedBy);
        assertEquals(resultApiKey.getExpiresAt(), testApiKey.getExpiresAt());
        assertEquals(resultApiKey.getApiKey(), testApiKey.getApiKey());
    }

    @Test
    void testDeleteApiKeyByHash() {
        deletableApiKey = ApiKey.builder()
                        .id(UUID.randomUUID().toString())
                        .apiKey(UUID.randomUUID().toString())
                        .expiresAt(StandardizedLocalDateTime.now().plusMinutes(5L))
                        .updatedBy(mockedUpdatedBy)
                        .build();

        apiKeyRepository.createApiKey(deletableApiKey);

        ApiKey found = apiKeyRepository.getApiKeyByHash(deletableApiKey.getApiKey());
        assertNotNull(found);
        assertEquals(deletableApiKey.getId(), found.getId());

        boolean deleted = apiKeyRepository.deleteApiKeyByHash(deletableApiKey.getApiKey());
        assertTrue(deleted);

        ApiKey deletedFetched = apiKeyRepository.getApiKeyByHash(deletableApiKey.getApiKey());
        assertNull(deletedFetched);
    }
}
