package com.patina.codebloom.db.discord.discordClub;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.time.OffsetDateTime;
import java.util.UUID;

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
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.discordClub.DiscordClubSqlRepository;
import com.patina.codebloom.common.db.repos.discord.discordClubMetadata.DiscordClubMetadataSqlRepository;
import com.patina.codebloom.db.BaseRepositoryTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubSqlRepositoryTest extends BaseRepositoryTest {

    private DiscordClubSqlRepository repo;
    private DiscordClubMetadataSqlRepository metadataRepo;

    private DiscordClub testDiscordClub;
    private DiscordClubMetadata testDiscordClubMetadata;

    private DiscordClub deletableDiscordClub;
    private DiscordClubMetadata deleteableDiscordClubMetadata;

    @Autowired
    public DiscordClubSqlRepositoryTest(final DiscordClubSqlRepository repo, final DiscordClubMetadataSqlRepository metadataRepo) {
        this.repo = repo;
        this.metadataRepo = metadataRepo;
    }

    @BeforeAll
    void createDiscordClub() {
        long timestamp = System.currentTimeMillis();

        testDiscordClub = DiscordClub.builder()
                        .id(UUID.randomUUID().toString())
                        .name("Test Discord Club")
                        .description("Integration test Discord Club at " + timestamp)
                        .tag(Tag.Nyu)
                        .createdAt(OffsetDateTime.now())
                        .build();

        repo.createDiscordClub(testDiscordClub);

        testDiscordClubMetadata = DiscordClubMetadata.builder()
                        .guildId("test guildId")
                        .leaderboardChannelId("test leaderboardChannelId")
                        .discordClubId(testDiscordClub.getId())
                        .build();

        metadataRepo.createDiscordClubMetadata(testDiscordClubMetadata);

        testDiscordClub = repo.getDiscordClubById(testDiscordClub.getId());
        assertNotNull(testDiscordClub.getDiscordClubMetadata(), "Metadata should be attached to the club after fetching.");
        assertEquals("test guildId", testDiscordClub.getDiscordClubMetadata().getGuildId());
        assertEquals("test guildId", testDiscordClubMetadata.getGuildId());
    }

    @AfterAll
    void cleanUp() {
        if (testDiscordClubMetadata != null && testDiscordClubMetadata.getId() != null) {
            boolean isSuccessful = metadataRepo.deleteDiscordClubMetadataById(testDiscordClubMetadata.getId());
            if (!isSuccessful) {
                fail("Failed deleting discord club metadata by id.");
            }
        }

        if (testDiscordClub != null && testDiscordClub.getId() != null) {
            boolean isSuccessful = repo.deleteDiscordClubById(testDiscordClub.getId());
            if (!isSuccessful) {
                fail("Failed deleting discord club by id.");
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
    void testGetMetadataByClubId() {
        String clubId = testDiscordClub.getId();
        assertNotNull(clubId, "Discord club id should not be null.");

        DiscordClubMetadata foundMetadata = metadataRepo.getMetadataByClubId(clubId);

        assertNotNull(foundMetadata, "Metadata should be found for the given club ID.");
        assertEquals(testDiscordClubMetadata.getGuildId(), foundMetadata.getGuildId());
        assertEquals(testDiscordClubMetadata.getLeaderboardChannelId(), foundMetadata.getLeaderboardChannelId());
        assertEquals(clubId, foundMetadata.getDiscordClubId());
    }

    @Test
    @Order(3)
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
    @Order(4)
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
