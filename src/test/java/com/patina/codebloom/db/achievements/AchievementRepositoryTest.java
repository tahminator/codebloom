package com.patina.codebloom.db.achievements;

// CHECKSTYLE:OFF
import static org.junit.jupiter.api.Assertions.*;
// CHECKSTYLE:ON

import java.util.List;

// CHECKSTYLE:OFF
import org.junit.jupiter.api.*;
// CHECKSTYLE:ON
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.achievements.Achievement;
import com.patina.codebloom.common.db.models.achievements.AchievementPlaceEnum;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.achievements.AchievementSqlRepository;
import com.patina.codebloom.db.BaseRepositoryTest;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class AchievementRepositoryTest extends BaseRepositoryTest {

    private AchievementSqlRepository repo;
    private Achievement testAchievement;
    private Achievement deletableAchievement;
    private String mockUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @Autowired
    public AchievementRepositoryTest(final AchievementSqlRepository repo) {
        this.repo = repo;
    }

    @BeforeAll
    void createAchievement() {
        testAchievement = Achievement.builder()
                        .userId(mockUserId)
                        .place(AchievementPlaceEnum.ONE)
                        .leaderboard(null)
                        .title("Test Achievement")
                        .description("Integration test achievement")
                        .isActive(true)
                        .createdAt(StandardizedOffsetDateTime.now())
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
        assertEquals(testAchievement, found);
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
                        .place(AchievementPlaceEnum.THREE)
                        .leaderboard(Tag.Patina)
                        .title("Updated Title")
                        .description("Updated Description")
                        .isActive(false)
                        .createdAt(testAchievement.getCreatedAt())
                        .deletedAt(testAchievement.getDeletedAt())
                        .build();

        Achievement result = repo.updateAchievement(updatedAchievement);

        assertNotNull(result);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Updated Description", result.getDescription());
        assertFalse(result.isActive());
    }

    @Test
    @Order(4)
    void testDeleteAchievementById() {
        deletableAchievement = Achievement.builder()
                        .userId(mockUserId)
                        .place(AchievementPlaceEnum.ONE)
                        .leaderboard(null)
                        .title("Deletable Achievement")
                        .description("Should be deleted")
                        .isActive(true)
                        .createdAt(StandardizedOffsetDateTime.now())
                        .deletedAt(null)
                        .build();

        repo.createAchievement(deletableAchievement);

        Achievement found = repo.getAchievementById(deletableAchievement.getId());
        assertNotNull(found);
        assertEquals(deletableAchievement.getId(), found.getId());

        boolean deleted = repo.deleteAchievementById(deletableAchievement.getId());
        assertTrue(deleted);

        Achievement deletedFetched = repo.getAchievementById(deletableAchievement.getId());
        assertNull(deletedFetched);
    }
}
