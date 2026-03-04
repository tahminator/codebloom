package org.patinanetwork.codebloom.common.db.repos.user;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.patinanetwork.codebloom.common.db.models.user.User;
import org.patinanetwork.codebloom.common.db.models.user.UserMetrics;
import org.patinanetwork.codebloom.common.db.repos.BaseRepositoryTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class UserMetricsRepositoryTest extends BaseRepositoryTest {

    private UserMetricsRepository userMetricsRepository;
    private UserRepository userRepository;

    private User testUser;
    private UserMetrics testUserMetrics;

    @Autowired
    public UserMetricsRepositoryTest(
            final UserMetricsRepository userMetricsRepository, final UserRepository userRepository) {
        this.userMetricsRepository = userMetricsRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll
    void setUp() {
        testUser = User.builder()
                .discordId("test-metrics-" + System.currentTimeMillis())
                .discordName("TestMetricsUser")
                .leetcodeUsername("testmetricsuser")
                .nickname("TestMetricsNickname")
                .admin(false)
                .schoolEmail("testmetrics@example.com")
                .profileUrl("")
                .tags(new ArrayList<>())
                .build();
        userRepository.createUser(testUser);

        testUserMetrics =
                UserMetrics.builder().userId(testUser.getId()).points(100).build();
        userMetricsRepository.createUserMetrics(testUserMetrics);

        if (testUserMetrics.getId() == null) {
            fail("UserMetrics id was not set after creation");
        }

        if (testUserMetrics.getCreatedAt() == null) {
            fail("UserMetrics createdAt was not set after creation");
        }
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = userRepository.deleteUserById(testUser.getId());
        if (!isSuccessful) {
            fail("Failed to delete test user");
        }
    }

    @Test
    @Order(1)
    void testCreateUserMetrics() {
        assertNotNull(testUserMetrics.getId());
        assertNotNull(testUserMetrics.getCreatedAt());
        assertEquals(testUser.getId(), testUserMetrics.getUserId());
        assertEquals(100, testUserMetrics.getPoints());
    }

    @Test
    @Order(2)
    void testFindUserMetricsById() {
        Optional<UserMetrics> found = userMetricsRepository.findUserMetricsById(testUserMetrics.getId());

        assertTrue(found.isPresent());
        assertEquals(testUserMetrics, found.get());
    }

    @Test
    @Order(3)
    void testFindUserMetricsByUserId() {
        List<UserMetrics> results = userMetricsRepository.findUserMetricsByUserId(testUser.getId());

        assertNotNull(results);
        assertTrue(results.size() > 0);
        assertTrue(results.stream().anyMatch(m -> m.getId().equals(testUserMetrics.getId())));
    }

    @Test
    @Order(4)
    void testDeleteUserMetricsById() {
        UserMetrics metricsToDelete =
                UserMetrics.builder().userId(testUser.getId()).points(50).build();
        userMetricsRepository.createUserMetrics(metricsToDelete);
        assertNotNull(metricsToDelete.getId());

        String idToDelete = metricsToDelete.getId();

        Optional<UserMetrics> existing = userMetricsRepository.findUserMetricsById(idToDelete);
        assertTrue(existing.isPresent());

        boolean deleteSuccess = userMetricsRepository.deleteUserMetricsById(idToDelete);
        assertTrue(deleteSuccess);

        Optional<UserMetrics> deleted = userMetricsRepository.findUserMetricsById(idToDelete);
        assertFalse(deleted.isPresent());

        boolean deleteAgain = userMetricsRepository.deleteUserMetricsById(idToDelete);
        assertFalse(deleteAgain);
    }

    @Test
    @Order(5)
    void testFindUserMetricsByUserIdExcludesSoftDeleted() {
        UserMetrics metricsToDelete =
                UserMetrics.builder().userId(testUser.getId()).points(75).build();
        userMetricsRepository.createUserMetrics(metricsToDelete);

        userMetricsRepository.deleteUserMetricsById(metricsToDelete.getId());

        List<UserMetrics> results = userMetricsRepository.findUserMetricsByUserId(testUser.getId());
        assertFalse(results.stream().anyMatch(m -> m.getId().equals(metricsToDelete.getId())));
    }

    @Test
    @Order(6)
    void testFindUserMetricsByIdWithInvalidUUID() {
        assertThrows(RuntimeException.class, () -> userMetricsRepository.findUserMetricsById("invalid-uuid-format"));
    }

    @Test
    @Order(7)
    void testFindUserMetricsByUserIdWithInvalidUUID() {
        assertThrows(
                RuntimeException.class, () -> userMetricsRepository.findUserMetricsByUserId("invalid-uuid-format"));
    }

    @Test
    @Order(8)
    void testDeleteUserMetricsByIdWithInvalidUUID() {
        assertThrows(RuntimeException.class, () -> userMetricsRepository.deleteUserMetricsById("invalid-uuid-format"));
    }
}
