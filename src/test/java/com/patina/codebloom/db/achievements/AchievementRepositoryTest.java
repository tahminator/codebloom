package com.patina.codebloom.db.achievements;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.achievements.Achievement;
import com.patina.codebloom.common.db.repos.achivenents.AchievementSqlRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AchievementRepositoryTest {

    private AchievementSqlRepository repo;
    private Achievement testAchievement;
    private Achievement deletableAchievement;
    private String mockUserId = UUID.randomUUID().toString();
    private String mockUserId2 = UUID.randomUUID().toString();

    @Autowired
    public AchievementRepositoryTest(final AchievementSqlRepository repo) {
        this.repo = repo;
    }

    @BeforeAll
    void createAchievement() {
        testAchievement = Achievement.builder()
                        .userId(mockUserId)
                        .iconUrl("")
                        .title("Test Achievement")
                        .description("Integration test achievement")
                        .isActive(true)
                        .createdAt(OffsetDateTime.now())
                        .deletedAt(null)
                        .build();

        repo.createAchievement(testAchievement);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = repo.deleteAchievementById(testAchievement.getId());
        if (!isSuccessful) {
            fail("Failed deleting achievement by id.");
        }
    }

    @Test
    @Order(1)
    void testGetAchievementById() {
        Achievement found = repo.getAchievementById(testAchievement.getId());
        assertNotNull(found);
        assertEquals(testAchievement.getId(), found.getId());
    }

    @Test
    @Order(2)
    void testGetAchievementsByUserId() {
        List<Achievement> achievementList = repo.getAchievementsByUserId(mockUserId);
        assertNotNull(achievementList);
        assertFalse(achievementList.isEmpty());
        assertTrue(achievementList.stream().anyMatch(a -> a.getId().equals(testAchievement.getId())));
    }

    @Test
    @Order(3)
    void testUpdateAchievement() {
        Achievement updatedAchievement = Achievement.builder()
                        .id(testAchievement.getId())
                        .userId(testAchievement.getUserId())
                        .iconUrl(testAchievement.getIconUrl())
                        .title("Updated Title")
                        .description("Updated Description")
                        .isActive(false)
                        .createdAt(testAchievement.getCreatedAt())
                        .deletedAt(testAchievement.getDeletedAt())
                        .build();

        boolean updated = repo.updateAchievement(updatedAchievement);
        assertTrue(updated);

        Achievement fetched = repo.getAchievementById(testAchievement.getId());
        assertEquals("Updated Title", fetched.getTitle());
        assertEquals("Updated Description", fetched.getDescription());
        assertFalse(fetched.isActive());
    }

    @Test
    @Order(4)
    void testDeleteAchievementById() {
        deletableAchievement = Achievement.builder()
                        .userId(mockUserId2)
                        .iconUrl("https://test.com/icon2.png")
                        .title("Deletable Achievement")
                        .description("Should be deleted")
                        .isActive(true)
                        .createdAt(OffsetDateTime.now())
                        .deletedAt(null)
                        .build();

        repo.createAchievement(deletableAchievement);

        Achievement found = repo.getAchievementById(deletableAchievement.getId());
        assertNotNull(found);
        assertEquals(deletableAchievement.getId(), found.getId());

        boolean deleted = repo.deleteAchievementById(deletableAchievement.getId());
        assertTrue(deleted);

        Achievement deletedFetched = repo.getAchievementById(deletableAchievement.getId());
        assertNotNull(deletedFetched.getDeletedAt());
    }
}