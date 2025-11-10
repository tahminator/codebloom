package com.patina.codebloom.db.discord.discordClub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.OffsetDateTime;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.discordClub.DiscordClubSqlRepository;
import com.patina.codebloom.db.BaseRepositoryTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubSqlRepositoryTest extends BaseRepositoryTest {

    private DiscordClubSqlRepository repo;
    private DiscordClub testDiscordClub;
    private DiscordClub deletableDiscordClub;

    @Autowired
    public DiscordClubSqlRepositoryTest(final DiscordClubSqlRepository repo) {
        this.repo = repo;
    }

    @BeforeAll
    void createDiscordClub() {
        long timestamp = System.currentTimeMillis();
        testDiscordClub = DiscordClub.builder()
                        .name("Test Discord Club")
                        .description("Integration test Discord Club at " + timestamp)
                        .tag(Tag.Nyu)
                        .createdAt(OffsetDateTime.now())
                        .build();
        repo.createDiscordClub(testDiscordClub);
        testDiscordClub = repo.getDiscordClubById(testDiscordClub.getId());
        assertNotNull(testDiscordClub, "Test discord club should be created and retrievable.");
    }

    @AfterAll
    void cleanUp() {
        if (testDiscordClub != null && testDiscordClub.getId() != null) {
            boolean isSuccessful = repo.deleteDiscordClubById(testDiscordClub.getId());
            if (!isSuccessful) {
                fail("Failed deleting club by id.");
            }
        }
    }

    @Test
    @Order(1)
    void testGetDiscordClubById() {
        DiscordClub found = repo.getDiscordClubById(testDiscordClub.getId());
        assertNotNull(found);
        assertEquals(testDiscordClub.getId(), found.getId());
        assertEquals(testDiscordClub, found);
    }

    @Test
    @Order(2)
    void testUpdateDiscordClub() {
        DiscordClub updatedDiscordClub = DiscordClub.builder()
                        .id(testDiscordClub.getId())
                        .name("Updated test discord club name")
                        .description("Updated description")
                        .tag(Tag.Baruch)
                        .build();

        DiscordClub result = repo.updateDiscordClub(updatedDiscordClub);

        assertNotNull(result);
        assertEquals("Updated test discord club name", result.getName());
        assertEquals("Updated description", result.getDescription());
        assertEquals(Tag.Baruch, result.getTag());
    }

    @Test
    @Order(3)
    void testDeleteDiscordClubById() {
        long timestamp = System.currentTimeMillis();
        deletableDiscordClub = DiscordClub.builder()
                        .name("Deletable club")
                        .description("Should be deleted" + timestamp)
                        .tag(Tag.Ccny)
                        .build();

        repo.createDiscordClub(deletableDiscordClub);
        deletableDiscordClub = repo.getDiscordClubById(deletableDiscordClub.getId());

        DiscordClub found = repo.getDiscordClubById(deletableDiscordClub.getId());
        assertNotNull(found);
        assertEquals(deletableDiscordClub.getId(), found.getId());
        assertEquals(deletableDiscordClub.getName(), found.getName());

        boolean deleted = repo.deleteDiscordClubById(deletableDiscordClub.getId());
        assertTrue(deleted);

        DiscordClub deletedFetched = repo.getDiscordClubById(deletableDiscordClub.getId());
        assertNull(deletedFetched);
    }

}
