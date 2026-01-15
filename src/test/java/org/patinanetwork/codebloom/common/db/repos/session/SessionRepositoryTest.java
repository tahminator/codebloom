package org.patinanetwork.codebloom.common.db.repos.session;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
// import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.patinanetwork.codebloom.common.db.models.Session;
import org.patinanetwork.codebloom.common.db.repos.BaseRepositoryTest;
import org.patinanetwork.codebloom.common.time.StandardizedLocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class SessionRepositoryTest extends BaseRepositoryTest {

    private SessionRepository sessionRepository;
    private Session testSession;
    private String mockUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    @Autowired
    public SessionRepositoryTest(final SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @BeforeAll
    void createSession() {
        testSession = Session.builder()
                .userId(mockUserId)
                .expiresAt(StandardizedLocalDateTime.now().plusHours(1))
                .build();

        sessionRepository.createSession(testSession);
        log.info("Created test session with ID: {}", testSession.getId());
    }

    @AfterAll
    void deleteSession() {
        boolean isSuccessful = sessionRepository.deleteSessionById(testSession.getId());

        if (!isSuccessful) {
            fail("Failed to delete test announcement");
        } else {
            log.info("Deleted test session with ID: {}", testSession.getId());
        }
    }

    @Test
    void testGetSessionById() {
        Session found = sessionRepository.getSessionById(testSession.getId());
        assertNotNull(found);
        assertEquals(testSession.getId(), found.getId());
    }

    @Test
    void testGetSessionsByUserId() {
        List<Session> sessionList = sessionRepository.getSessionsByUserId(mockUserId);
        assertNotNull(sessionList);
        assertFalse(sessionList.isEmpty());
        assertTrue(sessionList.stream().anyMatch(session -> session.getId().equals(testSession.getId())));
    }
}
