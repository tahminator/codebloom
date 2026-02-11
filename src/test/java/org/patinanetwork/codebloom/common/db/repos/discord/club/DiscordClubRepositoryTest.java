package org.patinanetwork.codebloom.common.db.repos.discord.club;

import static org.junit.jupiter.api.Assertions.*;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClub;
import org.patinanetwork.codebloom.common.db.models.discord.DiscordClubMetadata;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.db.repos.BaseRepositoryTest;
import org.patinanetwork.codebloom.common.db.repos.discord.club.metadata.DiscordClubMetadataRepository;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubRepositoryTest extends BaseRepositoryTest {

    private DiscordClubRepository clubRepo;
    private DiscordClubMetadataRepository metaRepo;

    private DiscordClub testDiscordClub;
    private DiscordClubMetadata testDiscordClubMetadata;

    @Autowired
    public DiscordClubRepositoryTest(
            final DiscordClubRepository clubRepo, final DiscordClubMetadataRepository metaRepo) {
        this.clubRepo = clubRepo;
        this.metaRepo = metaRepo;
    }

    @BeforeAll
    void createDiscordClub() {
        long timestamp = System.currentTimeMillis();

        testDiscordClub = DiscordClub.builder()
                .name("Test Discord Club")
                .description(Optional.of("Integration test Discord Club at " + timestamp))
                .tag(Tag.Nyu)
                .createdAt(OffsetDateTime.now())
                .build();

        clubRepo.createDiscordClub(testDiscordClub);

        testDiscordClubMetadata = DiscordClubMetadata.builder()
                .guildId(Optional.of("123456789"))
                .discordClubId(testDiscordClub.getId())
                .build();

        testDiscordClub.setDiscordClubMetadata(Optional.of(testDiscordClubMetadata));

        metaRepo.createDiscordClubMetadata(testDiscordClubMetadata);
    }

    @AfterAll
    void cleanUp() {
        boolean metaDeleted = metaRepo.deleteDiscordClubMetadataById(testDiscordClubMetadata.getId());
        assertTrue(metaDeleted, "Failed deleting discord club metadata by id.");

        boolean clubDeleted = clubRepo.deleteDiscordClubById(testDiscordClub.getId());
        assertTrue(clubDeleted, "Failed deleting discord club by id.");
    }

    @Test
    @Order(1)
    void testGetDiscordClubById() {
        DiscordClub found = clubRepo.getDiscordClubById(testDiscordClub.getId()).orElseThrow();
        assertEquals(testDiscordClub, found);
    }

    @Test
    @Order(2)
    void testGetAllActiveDiscordClubs() {
        var clubs = clubRepo.getAllActiveDiscordClubs();
        assertTrue(clubs.contains(testDiscordClub));
    }

    @Test
    @Order(3)
    void testGetDiscordClubByGuildId() {

        DiscordClub found = clubRepo.getDiscordClubByGuildId("123456789").orElseThrow();
        assertEquals(testDiscordClub, found);
    }

    @Test
    @Order(4)
    void testUpdateDiscordClub() {
        testDiscordClub.setDescription(Optional.of("helloworld"));
        testDiscordClub.setDeletedAt(Optional.of(StandardizedOffsetDateTime.now()));

        boolean isUpdateSuccessful = clubRepo.updateDiscordClubById(testDiscordClub);
        assertTrue(isUpdateSuccessful);

        var newUpdatedClub =
                clubRepo.getDiscordClubById(testDiscordClub.getId()).orElseThrow();

        assertEquals(testDiscordClub, newUpdatedClub);
    }
}
