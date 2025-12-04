package com.patina.codebloom.db.discord.club;

import static org.junit.jupiter.api.Assertions.*;

import com.patina.codebloom.common.db.models.discord.DiscordClub;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.discord.club.DiscordClubRepository;
import com.patina.codebloom.common.time.StandardizedOffsetDateTime;
import com.patina.codebloom.db.BaseRepositoryTest;
import java.time.OffsetDateTime;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class DiscordClubRepositoryTest extends BaseRepositoryTest {

    private DiscordClubRepository repo;

    private DiscordClub testDiscordClub;

    @Autowired
    public DiscordClubRepositoryTest(final DiscordClubRepository repo) {
        this.repo = repo;
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

        repo.createDiscordClub(testDiscordClub);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = repo.deleteDiscordClubById(testDiscordClub.getId());
        assertTrue(isSuccessful, "Failed deleting discord club by id.");
    }

    @Test
    @Order(1)
    void testGetDiscordClubById() {
        DiscordClub found = repo.getDiscordClubById(testDiscordClub.getId()).orElseThrow();
        assertEquals(testDiscordClub, found);
    }

    @Test
    @Order(2)
    void testGetAllActiveDiscordClubs() {
        var clubs = repo.getAllActiveDiscordClubs();
        assertTrue(clubs.contains(testDiscordClub));
    }

    @Test
    @Order(3)
    void testUpdateDiscordClub() {
        testDiscordClub.setDescription(Optional.of("helloworld"));
        testDiscordClub.setDeletedAt(Optional.of(StandardizedOffsetDateTime.now()));

        boolean isUpdateSuccessful = repo.updateDiscordClubById(testDiscordClub);
        assertTrue(isUpdateSuccessful);

        var newUpdatedClub = repo.getDiscordClubById(testDiscordClub.getId()).orElseThrow();

        assertEquals(testDiscordClub, newUpdatedClub);
    }
}
