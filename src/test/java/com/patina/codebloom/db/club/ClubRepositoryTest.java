package com.patina.codebloom.db.club;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.club.Club;
import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.club.ClubSqlRepository;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
public class ClubRepositoryTest {

    private ClubSqlRepository repo;
    private Club testClub;
    private Club deletableClub;

    @Autowired
    public ClubRepositoryTest(final ClubSqlRepository repo) {
        this.repo = repo;
    }

    @BeforeAll
    void createClub() {
        long timestamp = System.currentTimeMillis();
        testClub = Club.builder()
                        .name("Test Club")
                        .description("Integration test club")
                        .slug("test-club-" + timestamp)
                        .splashIconUrl("")
                        .password("testpassword123")
                        .tag(Tag.Patina)
                        .build();

        repo.createClub(testClub);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = repo.deleteClubById(testClub.getId());
        if (!isSuccessful) {
            fail("Failed deleting club by id.");
        }
    }

    @Test
    @Order(1)
    void testGetClubById() {
        Club found = repo.getClubById(testClub.getId());
        assertNotNull(found);
        assertEquals(testClub.getId(), found.getId());
        assertEquals(testClub, found);
    }

    @Test
    @Order(2)
    void testGetClubBySlug() {
        Club found = repo.getClubBySlug(testClub.getSlug());
        assertNotNull(found);
        assertEquals(testClub.getSlug(), found.getSlug());
        assertEquals(testClub, found);
    }

    @Test
    @Order(3)
    void testUpdateClub() {
        Club updatedClub = Club.builder()
                        .id(testClub.getId())
                        .name("Updated Club Name")
                        .description("Updated Description")
                        .slug(testClub.getSlug())
                        .splashIconUrl("")
                        .password("newpassword456")
                        .tag(Tag.Hunter)
                        .build();

        Club result = repo.updateClub(updatedClub);

        assertNotNull(result);
        assertEquals("Updated Club Name", result.getName());
        assertEquals("Updated Description", result.getDescription());
        assertEquals("", result.getSplashIconUrl());
        assertEquals("newpassword456", result.getPassword());
        assertEquals(Tag.Hunter, result.getTag());
    }

    @Test
    @Order(4)
    void testDeleteClubById() {
        long timestamp = System.currentTimeMillis();
        deletableClub = Club.builder()
                        .name("Deletable Club")
                        .description("Should be deleted")
                        .slug("deletable-club-" + timestamp)
                        .splashIconUrl("")
                        .password("deletepassword")
                        .tag(Tag.Nyu)
                        .build();

        repo.createClub(deletableClub);

        Club found = repo.getClubById(deletableClub.getId());
        assertNotNull(found);
        assertEquals(deletableClub.getId(), found.getId());
        assertEquals(deletableClub, found);

        boolean deleted = repo.deleteClubById(deletableClub.getId());
        assertTrue(deleted);

        Club deletedFetched = repo.getClubById(deletableClub.getId());
        assertNull(deletedFetched);
    }

    @Test
    @Order(5)
    void testDeleteClubBySlug() {
        long timestamp = System.currentTimeMillis();
        Club deletableBySlugClub = Club.builder()
                        .name("Deletable By Slug Club")
                        .description("Should be deleted by slug")
                        .slug("deletable-by-slug-club-" + timestamp)
                        .splashIconUrl("")
                        .password("deleteslugpassword")
                        .tag(Tag.Baruch)
                        .build();

        repo.createClub(deletableBySlugClub);

        Club found = repo.getClubBySlug(deletableBySlugClub.getSlug());
        assertNotNull(found);
        assertEquals(deletableBySlugClub.getSlug(), found.getSlug());
        assertEquals(deletableBySlugClub, found);

        boolean deleted = repo.deleteClubBySlug(deletableBySlugClub.getSlug());
        assertTrue(deleted);

        Club deletedFetched = repo.getClubBySlug(deletableBySlugClub.getSlug());
        assertNull(deletedFetched);
    }

}