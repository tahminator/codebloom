package com.patina.codebloom.db.discord.discordClubMetadata;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.discord.DiscordClubMetadata;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.discordClub.DiscordClubSqlRepository;
import com.patina.codebloom.common.db.repos.discord.discordClubMetadata.DiscordClubMetadataSqlRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubMetadataSqlRepositoryTest {

    private DiscordClubMetadataSqlRepository repo;
    private DiscordClubSqlRepository discordClubRepo;
    private DiscordClubMetadata testDiscordClubMetadata;
    private DiscordClubMetadata deletableDiscordClubMetadata;

    @Autowired
    public DiscordClubMetadataSqlRepositoryTest(final DiscordClubMetadataSqlRepository repo, final DiscordClubSqlRepository discordClubRepo) {
        this.repo = repo;
        this.discordClubRepo = discordClubRepo;
    }

    @BeforeAll
    void createDiscordClubMetadata() {
        DiscordClub club = DiscordClub.builder()
                        .id(UUID.randomUUID().toString())
                        .name("test club")
                        .description("test parent club")
                        .tag(Tag.Cornell)
                        .build();

        discordClubRepo.createDiscordClub(club);
        String createdClubId = club.getId();

        testDiscordClubMetadata = DiscordClubMetadata.builder()
                        .guildId("Original guildId")
                        .leaderboardChannelId("Original leaderboardChannelId")
                        .discordClubId(createdClubId)
                        .build();
        repo.createDiscordClubMetadata(testDiscordClubMetadata);
        testDiscordClubMetadata = repo.getDiscordClubMetadataById(testDiscordClubMetadata.getId());
        assertNotNull(testDiscordClubMetadata, "Test discord club metadata should be created be retrievable.");
    }

    @AfterAll
    void cleanUp() {
        if (testDiscordClubMetadata != null && testDiscordClubMetadata.getId() != null) {
            boolean isSuccessful = repo.deleteDiscordClubMetadataById(testDiscordClubMetadata.getId());
            if (!isSuccessful) {
                fail("Failed deleting discord club metadata by id.");
            }
        }
    }

    @Test
    @Order(1)
    void testGetDiscordClubMetadataById() {
        DiscordClubMetadata found = repo.getDiscordClubMetadataById(testDiscordClubMetadata.getId());
        assertNotNull(found);
        assertEquals(testDiscordClubMetadata.getId(), found.getId());
        assertEquals(testDiscordClubMetadata, found);
    }

    @Test
    @Order(2)
    void testUpdateDiscordClubMetadata() {
        DiscordClub club = DiscordClub.builder()
                        .id(UUID.randomUUID().toString())
                        .name("updated test club")
                        .description("updated test parent club")
                        .tag(Tag.Bmcc)
                        .build();

        discordClubRepo.createDiscordClub(club);
        String createdClubId = club.getId();

        DiscordClubMetadata updatedDiscordClubMetadata = DiscordClubMetadata.builder()
                        .id(testDiscordClubMetadata.getId())
                        .guildId("Updated guildId")
                        .leaderboardChannelId("Updated leaderboardChannelId")
                        .discordClubId(createdClubId)
                        .build();

        DiscordClubMetadata result = repo.updateDiscordClubMetadata(updatedDiscordClubMetadata);

        assertNotNull(result);
        assertEquals("Updated guildId", result.getGuildId());
        assertEquals("Updated leaderboardChannelId", result.getLeaderboardChannelId());
        assertEquals(createdClubId, result.getDiscordClubId());
    }
}
