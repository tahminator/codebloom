package com.patina.codebloom.db.task;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.task.BackgroundTask;
import com.patina.codebloom.common.db.models.task.BackgroundTaskEnum;
import com.patina.codebloom.common.db.repos.task.BackgroundTaskRepository;
import com.patina.codebloom.common.db.repos.task.BackgroundTaskSqlRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class BackgroundTaskRepositoryTest extends BaseRepositoryTest {

    private BackgroundTaskRepository backgroundTaskRepository;
    private BackgroundTask testTask;

    @Autowired
    public BackgroundTaskRepositoryTest(final BackgroundTaskSqlRepository backgroundTaskSqlRepository) {
        this.backgroundTaskRepository = backgroundTaskSqlRepository;
    }

    @BeforeAll
    void createBackgroundTask() {
        testTask = BackgroundTask.builder()
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(StandardizedOffsetDateTime.now())
                        .build();

        backgroundTaskRepository.createBackgroundTask(testTask);
        assertNotNull(testTask, "Test background task should be created");
    }

    @AfterAll
    void cleanUp() {
        if (testTask != null && testTask.getId() != null) {
            boolean isSuccessful = backgroundTaskRepository.deleteBackgroundTaskById(testTask.getId());
            if (!isSuccessful) {
                throw new RuntimeException(
                                "Test task still exists after test completion: %s".formatted(testTask.getId()));
            }
        }
    }

    @Test
    @Order(1)
    void testGetBackgroundTaskById() {
        BackgroundTask found = backgroundTaskRepository.getBackgroundTaskById(testTask.getId());
        assertNotNull(found);
        assertEquals(testTask, found);
    }

    @Test
    @Order(2)
    void testGetBackgroundTasksByTaskEnum() {
        List<BackgroundTask> tasks = backgroundTaskRepository.getBackgroundTasksByTaskEnum(BackgroundTaskEnum.LEETCODE_QUESTION_BANK);
        assertNotNull(tasks);
        assertFalse(tasks.isEmpty());
        assertTrue(tasks.stream().anyMatch(task -> task.getId().equals(testTask.getId())));
        assertTrue(tasks.stream().allMatch(task -> task.getTask() == BackgroundTaskEnum.LEETCODE_QUESTION_BANK));
    }

    @Test
    @Order(3)
    void testGetMostRecentlyCompletedBackgroundTaskByTaskEnum() {
        BackgroundTask recentTask = backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(BackgroundTaskEnum.LEETCODE_QUESTION_BANK);
        assertNotNull(recentTask);
        assertEquals(BackgroundTaskEnum.LEETCODE_QUESTION_BANK, recentTask.getTask());
        assertNotNull(recentTask.getCompletedAt());
    }

    @Test
    @Order(4)
    void testUpdateBackgroundTaskById() {
        OffsetDateTime newCompletedAt = StandardizedOffsetDateTime.now().plusMinutes(30);
        BackgroundTask updatedTask = BackgroundTask.builder()
                        .id(testTask.getId())
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(newCompletedAt)
                        .build();

        boolean updateResult = backgroundTaskRepository.updateBackgroundTaskById(updatedTask);
        assertTrue(updateResult);

        BackgroundTask retrieved = backgroundTaskRepository.getBackgroundTaskById(testTask.getId());
        assertNotNull(retrieved);
        assertEquals(BackgroundTaskEnum.LEETCODE_QUESTION_BANK, retrieved.getTask());
        assertEquals(newCompletedAt, retrieved.getCompletedAt());
    }

    @Test
    @Order(5)
    void testCreateAnotherBackgroundTask() {
        BackgroundTask anotherTask = BackgroundTask.builder()
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(OffsetDateTime.now().minusHours(1))
                        .build();

        backgroundTaskRepository.createBackgroundTask(anotherTask);
        assertNotNull(anotherTask.getId());

        BackgroundTask found = backgroundTaskRepository.getBackgroundTaskById(anotherTask.getId());
        assertNotNull(found);
        assertEquals(anotherTask.getTask(), found.getTask());
        assertEquals(anotherTask, found);
    }

    @Test
    @Order(6)
    void testGetBackgroundTaskByIdNotFound() {
        BackgroundTask notFound = backgroundTaskRepository.getBackgroundTaskById(UUID.randomUUID().toString());
        assertNull(notFound);
    }

    @Test
    @Order(7)
    void testUpdateBackgroundTaskByIdNotFound() {
        BackgroundTask nonExistentTask = BackgroundTask.builder()
                        .id(UUID.randomUUID().toString())
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(OffsetDateTime.now())
                        .build();

        boolean updateResult = backgroundTaskRepository.updateBackgroundTaskById(nonExistentTask);
        assertFalse(updateResult);
    }

    @Test
    @Order(8)
    void testCreateBackgroundTaskWithNullCompletedAt() {
        BackgroundTask taskWithNullCompletedAt = BackgroundTask.builder()
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(null)
                        .build();

        backgroundTaskRepository.createBackgroundTask(taskWithNullCompletedAt);
        assertNotNull(taskWithNullCompletedAt.getId());

        BackgroundTask retrieved = backgroundTaskRepository.getBackgroundTaskById(taskWithNullCompletedAt.getId());
        assertNotNull(retrieved);
        assertNotNull(retrieved.getCompletedAt(), "completedAt should be set to current time when null");
    }

    @Test
    @Order(9)
    void testCreateBackgroundTaskWithNullTaskEnum() {
        BackgroundTask taskWithNullEnum = BackgroundTask.builder()
                        .task(null)
                        .completedAt(StandardizedOffsetDateTime.now())
                        .build();

        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.createBackgroundTask(taskWithNullEnum);
        }, "Creating task with null enum should throw exception");
    }

    @Test
    @Order(10)
    void testGetBackgroundTaskByIdWithNullId() {
        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.getBackgroundTaskById(null);
        }, "Getting task with null ID should throw exception");
    }

    @Test
    @Order(11)
    void testGetBackgroundTasksByTaskEnumWithNull() {
        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.getBackgroundTasksByTaskEnum(null);
        }, "Getting tasks with null enum should throw exception");
    }

    @Test
    @Order(12)
    void testGetMostRecentlyCompletedBackgroundTaskByTaskEnumWithNull() {
        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.getMostRecentlyCompletedBackgroundTaskByTaskEnum(null);
        }, "Getting most recent task with null enum should throw exception");
    }

    @Test
    @Order(13)
    void testUpdateBackgroundTaskByIdWithNull() {
        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.updateBackgroundTaskById(null);
        }, "Updating with null task should throw exception");
    }

    @Test
    @Order(14)
    void testDeleteBackgroundTaskById() {
        BackgroundTask deletableTask = BackgroundTask.builder()
                        .task(BackgroundTaskEnum.LEETCODE_QUESTION_BANK)
                        .completedAt(StandardizedOffsetDateTime.now())
                        .build();

        backgroundTaskRepository.createBackgroundTask(deletableTask);
        assertNotNull(deletableTask.getId());

        BackgroundTask found = backgroundTaskRepository.getBackgroundTaskById(deletableTask.getId());
        assertNotNull(found);
        assertEquals(deletableTask, found);

        boolean deleted = backgroundTaskRepository.deleteBackgroundTaskById(deletableTask.getId());
        assertTrue(deleted);

        BackgroundTask deletedTask = backgroundTaskRepository.getBackgroundTaskById(deletableTask.getId());
        assertNull(deletedTask);
    }

    @Test
    @Order(15)
    void testDeleteBackgroundTaskByIdNotFound() {
        boolean deleted = backgroundTaskRepository.deleteBackgroundTaskById(UUID.randomUUID().toString());
        assertFalse(deleted);
    }

    @Test
    @Order(16)
    void testDeleteBackgroundTaskByIdWithNull() {
        assertThrows(Exception.class, () -> {
            backgroundTaskRepository.deleteBackgroundTaskById(null);
        }, "Deleting with null ID should throw exception");
    }
}
