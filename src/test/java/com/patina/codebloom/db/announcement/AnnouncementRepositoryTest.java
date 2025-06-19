package com.patina.codebloom.db.announcement;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.announcement.Announcement;
import com.patina.codebloom.common.db.repos.announcement.AnnouncementRepository;
import com.patina.codebloom.common.time.StandardizedLocalDateTime;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class AnnouncementRepositoryTest {
    private AnnouncementRepository announcementRepository;
    private Announcement testAnnouncement;

    @Autowired
    public AnnouncementRepositoryTest(final AnnouncementRepository announcementRepository) {
        this.announcementRepository = announcementRepository;
    }

    @BeforeAll
    void createTestAnnouncement() {
        testAnnouncement = Announcement.builder()
                        // id will be set by announcementRepository
                        .id(null)
                        .expiresAt(StandardizedLocalDateTime.now().plusMinutes(5L))
                        .showTimer(true)
                        .message("Hi this is a test announcement!")
                        .build();
        boolean isSuccessful = announcementRepository.createAnnouncement(testAnnouncement);

        if (!isSuccessful) {
            fail("Failed to create test announcement");
        }
    }

    @AfterAll
    void deleteTestAnnouncement() {
        boolean isSuccessful = announcementRepository.deleteAnnouncementById(testAnnouncement.getId());

        if (!isSuccessful) {
            fail("Failed to delete test announcement");
        }
    }

    /**
     * @note this could possibly fail via race condition if you create a new
     * announcement in the few seconds while this test suite is running, overriding
     * the test announcement. If you do, let the test suite finish and let
     * `@AfterAll` delete the test announcement and then just run the test suite
     * again.
     *
     * We run this test first just to make sure that we don't run into that issue.
     */
    @Test
    @Order(1)
    void findMostRecentAnnouncement() {
        Announcement possibleTestAnnouncement = announcementRepository.getRecentAnnouncement();

        if (possibleTestAnnouncement == null) {
            fail("most recent announcement doesn't exist, even though we created a test announcement");
        }

        log.info("testAnnouncement: {}", testAnnouncement.toString());
        log.info("possibleTestAnnouncement: {}", possibleTestAnnouncement.toString());

        /**
         * @note - see method note
         */
        if (!testAnnouncement.equals(possibleTestAnnouncement)) {
            fail("the most recent announcement does not equal to the test announcement");
        }
    }

    @Test
    void findAnnouncementById() {
        Announcement possibleTestAnnouncement = announcementRepository.getAnnouncementById(testAnnouncement.getId());

        if (possibleTestAnnouncement == null) {
            fail("failed to find announcement by id");
        }

        log.info("testAnnouncement: {}", testAnnouncement.toString());
        log.info("possibleTestAnnouncement: {}", possibleTestAnnouncement.toString());

        if (!testAnnouncement.equals(possibleTestAnnouncement)) {
            fail("the generated test announcement does not match the announcement fetched with get announcement by ID");
        }
    }

    @Test
    void findAllAnnouncements() {
        List<Announcement> announcementsList = announcementRepository.getAllAnnouncements();

        if (announcementsList == null) {
            fail("list of all announcements is null");
        }

        if (announcementsList.size() == 0) {
            fail("list of all announcements is 0, when it should be atleast 1 from the test announcement");
        }

        log.info(announcementsList.toString());
        if (!announcementsList.contains(testAnnouncement)) {
            fail("test announcement cannot be found in the list of all announcements");
        }
    }

}
