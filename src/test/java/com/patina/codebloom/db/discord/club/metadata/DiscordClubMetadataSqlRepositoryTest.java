package com.patina.codebloom.db.discord.club.metadata;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

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
import com.patina.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import com.patina.codebloom.common.db.repos.discord.club.metadata.DiscordClubMetadataRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubMetadataSqlRepositoryTest {

    private DiscordClubMetadataRepository repo;
    private DiscordClubRepository discordClubRepo;

    private DiscordClub testDiscordClub;
    private DiscordClubMetadata testDiscordClubMetadata;

    @Autowired
    public DiscordClubMetadataSqlRepositoryTest(final DiscordClubMetadataRepository repo, final DiscordClubRepository discordClubRepo) {
        this.repo = repo;
        this.discordClubRepo = discordClubRepo;
    }

    @BeforeAll
    void createDiscordClubMetadata() {
        testDiscordClub = DiscordClub.builder()
                        .name("test club")
                        .description(Optional.of("test parent club"))
                        .tag(Tag.Cornell)
                        .build();

        discordClubRepo.createDiscordClub(testDiscordClub);

        testDiscordClubMetadata = DiscordClubMetadata.builder()
                        .leaderboardChannelId(Optional.of("Original leaderboardChannelId"))
                        .discordClubId(testDiscordClub.getId())
                        .build();

        repo.createDiscordClubMetadata(testDiscordClubMetadata);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = repo.deleteDiscordClubMetadataById(testDiscordClubMetadata.getId());
        assertTrue(isSuccessful, "Failed deleting discord club metadata by id.");

        isSuccessful = discordClubRepo.deleteDiscordClubById(testDiscordClub.getId());
        assertTrue(isSuccessful, "Failed deleting discord club by id.");
    }

    @Test
    @Order(1)
    void testGetDiscordClubMetadataById() {
        DiscordClubMetadata found = repo.getDiscordClubMetadataById(testDiscordClubMetadata.getId()).orElseThrow();
        assertEquals(testDiscordClubMetadata, found);
    }

    @Test
    @Order(2)
    void testGetDiscordClubMetadataByClubId() {
        var possibleClubMetadata = repo.getDiscordClubMetadataByClubId(testDiscordClub.getId()).orElseThrow();
        assertEquals(testDiscordClubMetadata, possibleClubMetadata);
    }

    @Test
    @Order(3)
    void testUpdateDiscordClubMetadata() {
        testDiscordClubMetadata.setGuildId(Optional.of("Original guildId"));

        boolean isUpdateSuccessful = repo.updateDiscordClubMetadata(testDiscordClubMetadata);
        assertTrue(isUpdateSuccessful);

        var updatedClubMetadata = repo.getDiscordClubMetadataById(testDiscordClubMetadata.getId()).orElseThrow();

        assertEquals(testDiscordClubMetadata, updatedClubMetadata);
    }
}
