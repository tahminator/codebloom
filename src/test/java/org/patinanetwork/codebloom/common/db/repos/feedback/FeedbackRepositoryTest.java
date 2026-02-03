package org.patinanetwork.codebloom.common.db.repos.feedback;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.patinanetwork.codebloom.common.db.models.feedback.Feedback;
import org.patinanetwork.codebloom.common.db.repos.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class FeedbackRepositoryTest extends BaseRepositoryTest {

    private final FeedbackRepository feedbackRepository;
    private Feedback testFeedback;

    @Autowired
    public FeedbackRepositoryTest(final FeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }

    @BeforeAll
    void createTestFeedback() {
        testFeedback = Feedback.builder()
                .title("Test Feedback Title")
                .description("This is a test feedback description")
                .email(Optional.of("test@example.com"))
                .build();

        feedbackRepository.createFeedback(testFeedback);

        if (testFeedback.getId() == null) {
            fail("Feedback id was not set after creation");
        }

        if (testFeedback.getCreatedAt() == null) {
            fail("Feedback createdAt was not set after creation");
        }
    }

    @AfterAll
    void deleteTestFeedback() {
        boolean isSuccessful = feedbackRepository.deleteFeedbackById(testFeedback.getId());

        if (!isSuccessful) {
            fail("Failed to delete test feedback");
        }
    }

    @Test
    @Order(1)
    void testCreateFeedback() {
        assertNotNull(testFeedback.getId(), "Feedback id should not be null");
        assertNotNull(testFeedback.getTitle(), "Feedback title should not be null");
        assertNotNull(testFeedback.getDescription(), "Feedback description should not be null");
        assertNotNull(testFeedback.getEmail(), "Feedback email Optional should not be null");
        assertNotNull(testFeedback.getCreatedAt(), "Feedback createdAt should not be null");

        assertEquals("Test Feedback Title", testFeedback.getTitle(), "Title should match");
        assertEquals("This is a test feedback description", testFeedback.getDescription(), "Description should match");
        assertTrue(testFeedback.getEmail().isPresent(), "Email should be present");
        assertEquals("test@example.com", testFeedback.getEmail().get(), "Email should match");

        log.info("Test feedback created: {}", testFeedback.toString());
    }

    @Test
    @Order(2)
    void testGetFeedbackById() {
        Optional<Feedback> retrievedFeedback = feedbackRepository.findFeedbackById(testFeedback.getId());

        assertTrue(retrievedFeedback.isPresent(), "Retrieved feedback should be present");
        assertEquals(testFeedback.getId(), retrievedFeedback.get().getId(), "Feedback id should match");
        assertEquals(testFeedback.getTitle(), retrievedFeedback.get().getTitle(), "Title should match");
        assertEquals(
                testFeedback.getDescription(), retrievedFeedback.get().getDescription(), "Description should match");
        assertEquals(testFeedback.getEmail(), retrievedFeedback.get().getEmail(), "Email should match");
        assertEquals(testFeedback.getCreatedAt(), retrievedFeedback.get().getCreatedAt(), "CreatedAt should match");

        log.info("Test feedback retrieved: {}", retrievedFeedback.get().toString());
    }

    @Test
    @Order(3)
    void testDeleteFeedbackById() {
        Feedback feedbackToDelete = Feedback.builder()
                .title("Feedback to Delete")
                .description("This feedback will be deleted")
                .email(Optional.of("delete@example.com"))
                .build();

        feedbackRepository.createFeedback(feedbackToDelete);
        assertNotNull(feedbackToDelete.getId(), "Feedback ID should be set after creation");

        String feedbackIdToDelete = feedbackToDelete.getId();

        Optional<Feedback> existingFeedback = feedbackRepository.findFeedbackById(feedbackIdToDelete);
        assertTrue(existingFeedback.isPresent(), "Feedback should exist before deletion");

        boolean deleteSuccess = feedbackRepository.deleteFeedbackById(feedbackIdToDelete);
        assertTrue(deleteSuccess, "Deleting feedback should succeed");

        Optional<Feedback> deletedFeedback = feedbackRepository.findFeedbackById(feedbackIdToDelete);
        assertFalse(deletedFeedback.isPresent(), "Deleted feedback should not be retrievable");

        log.info("Test feedback deleted successfully");
    }

    @Test
    @Order(4)
    void testUpdateFeedback() {
        testFeedback.setTitle("Updated Feedback Title");
        testFeedback.setDescription("This is an updated description");
        testFeedback.setEmail(Optional.of("updated@example.com"));

        boolean isSuccessful = feedbackRepository.updateFeedback(testFeedback);

        assertTrue(isSuccessful, "Updating feedback should succeed");

        Optional<Feedback> updatedFeedback = feedbackRepository.findFeedbackById(testFeedback.getId());

        assertTrue(updatedFeedback.isPresent(), "Updated feedback should be present");
        assertEquals("Updated Feedback Title", updatedFeedback.get().getTitle(), "Title should be updated");
        assertEquals(
                "This is an updated description",
                updatedFeedback.get().getDescription(),
                "Description should be updated");
        assertTrue(updatedFeedback.get().getEmail().isPresent(), "Email should be present");
        assertEquals("updated@example.com", updatedFeedback.get().getEmail().get(), "Email should be updated");

        log.info("Test feedback updated: {}", updatedFeedback.get().toString());
    }

    @Test
    @Order(5)
    void testGetFeedbackByIdWithInvalidUUID() {
        String invalidUUID = "invalid-uuid-format";
        assertThrows(
                RuntimeException.class,
                () -> {
                    feedbackRepository.findFeedbackById(invalidUUID);
                },
                "Should throw RuntimeException for invalid UUID format");
        log.info("Successfully handled invalid UUID format");
    }

    @Test
    @Order(6)
    void testDeleteFeedbackByIdWithInvalidUUID() {
        String invalidUUID = "invalid-uuid-format";
        assertThrows(
                RuntimeException.class,
                () -> {
                    feedbackRepository.deleteFeedbackById(invalidUUID);
                },
                "Should throw RuntimeException for invalid UUID format");
        log.info("Successfully handled invalid UUID format in delete");
    }

    @Test
    @Order(7)
    void testUpdateFeedbackWithInvalidUUID() {
        Feedback invalidFeedback = Feedback.builder()
                .id("invalid-uuid-format")
                .title("Invalid Feedback")
                .description("This feedback has invalid UUID")
                .email(Optional.of("invalid@example.com"))
                .build();
        assertThrows(
                RuntimeException.class,
                () -> {
                    feedbackRepository.updateFeedback(invalidFeedback);
                },
                "Should throw RuntimeException for invalid UUID format");
        log.info("Successfully handled invalid UUID format in update");
    }

    @Test
    @Order(8)
    void testUpdateFeedbackWithNullEmail() {
        Feedback feedbackToUpdate = Feedback.builder()
                .title("Feedback For Email Update Test")
                .description("Testing email update to null")
                .email(Optional.of("initial@example.com"))
                .build();
        feedbackRepository.createFeedback(feedbackToUpdate);
        feedbackToUpdate.setEmail(Optional.empty());
        boolean updateSuccess = feedbackRepository.updateFeedback(feedbackToUpdate);
        assertTrue(updateSuccess, "Updating email to null should succeed");
        Optional<Feedback> updatedFeedback = feedbackRepository.findFeedbackById(feedbackToUpdate.getId());
        assertTrue(updatedFeedback.isPresent(), "Updated feedback should be present");
        assertFalse(updatedFeedback.get().getEmail().isPresent(), "Email should be empty after update");
        feedbackRepository.deleteFeedbackById(feedbackToUpdate.getId());
        log.info("Successfully updated feedback email to null");
    }

    @Test
    @Order(9)
    void testCreateFeedbackWithSpecialCharacters() {
        Feedback feedbackWithSpecialChars = Feedback.builder()
                .title("Special !@#$%^&*() Title")
                .description("Description with émojis 🎉 and spécial çharacters: <>&\"'")
                .email(Optional.of("special+chars@example.com"))
                .build();
        feedbackRepository.createFeedback(feedbackWithSpecialChars);
        assertNotNull(feedbackWithSpecialChars.getId(), "Feedback ID should be set after creation");
        Optional<Feedback> retrievedFeedback = feedbackRepository.findFeedbackById(feedbackWithSpecialChars.getId());
        assertTrue(retrievedFeedback.isPresent(), "Feedback should be retrievable");
        assertEquals(
                feedbackWithSpecialChars.getTitle(),
                retrievedFeedback.get().getTitle(),
                "Special characters in title should be preserved");
        assertEquals(
                feedbackWithSpecialChars.getDescription(),
                retrievedFeedback.get().getDescription(),
                "Special characters in description should be preserved");
        assertEquals(
                feedbackWithSpecialChars.getEmail(),
                retrievedFeedback.get().getEmail(),
                "Special characters in email should be preserved");
        feedbackRepository.deleteFeedbackById(feedbackWithSpecialChars.getId());
        log.info("Successfully created and retrieved feedback with special characters");
    }

    @Test
    @Order(10)
    void testCreateFeedbackWithNullEmail() {
        Feedback feedbackWithNullEmail = Feedback.builder()
                .title("Feedback Without Email")
                .description("This feedback has no email")
                .email(Optional.empty())
                .build();
        feedbackRepository.createFeedback(feedbackWithNullEmail);
        assertNotNull(feedbackWithNullEmail.getId(), "Feedback ID should be set after creation");
        Optional<Feedback> retrievedFeedback = feedbackRepository.findFeedbackById(feedbackWithNullEmail.getId());
        assertTrue(retrievedFeedback.isPresent(), "Feedback should be retrievable");
        assertFalse(retrievedFeedback.get().getEmail().isPresent(), "Email should be empty");
        assertEquals("Feedback Without Email", retrievedFeedback.get().getTitle());
        feedbackRepository.deleteFeedbackById(feedbackWithNullEmail.getId());
        log.info("Successfully created feedback with null email");
    }
}
