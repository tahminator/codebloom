package com.patina.codebloom.db.leaderboard;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.leaderboard.Leaderboard;
import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.models.user.UserWithScore;
import com.patina.codebloom.common.db.repos.leaderboard.LeaderboardRepository;
import com.patina.codebloom.common.db.repos.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class LeaderboardRepositoryTest {
    private Leaderboard previousLeaderboard;
    private final LeaderboardRepository leaderboardRepository;
    private final UserRepository userRepository;
    private final String superUserId = "ed3bfe18-e42a-467f-b4fa-07e8da4d2555";

    static final int PAGE_SIZE = 9999999;
    private Leaderboard mockLeaderboard;

    @Autowired
    public LeaderboardRepositoryTest(final LeaderboardRepository leaderboardRepository,
                    final UserRepository userRepository) {
        this.leaderboardRepository = leaderboardRepository;
        this.userRepository = userRepository;
    }

    @BeforeAll
    void createMockLeaderboard() {
        // will bring this back after
        previousLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();
        mockLeaderboard = Leaderboard.builder()
                        .name("Mock Leaderboard")
                        .build();
        leaderboardRepository.addNewLeaderboard(mockLeaderboard);
    }

    @AfterAll
    void deleteMockLeaderboard() throws Exception {
        Method deleteLeaderboardMethod = leaderboardRepository.getClass().getDeclaredMethod("deleteLeaderboardById", String.class);
        Method enableLeaderboardMethod = leaderboardRepository.getClass().getDeclaredMethod("enableLeaderboardById", String.class);

        deleteLeaderboardMethod.setAccessible(true);
        enableLeaderboardMethod.setAccessible(true);

        boolean deleteLeaderboardSuccessful = (Boolean) deleteLeaderboardMethod.invoke(leaderboardRepository, mockLeaderboard.getId());

        assertTrue(deleteLeaderboardSuccessful, "failed to delete mock leaderboard");

        boolean enableLeaderboardSuccessful = (Boolean) enableLeaderboardMethod.invoke(leaderboardRepository, previousLeaderboard.getId());

        assertTrue(enableLeaderboardSuccessful, "failed to restore previous leaderboard");
    }

    @Order(1)
    @Test
    void testIfMostRecentLeaderboardMetadataValid() {
        Leaderboard possiblyMockLeaderboard = leaderboardRepository.getRecentLeaderboardMetadata();

        if (possiblyMockLeaderboard == null || !mockLeaderboard.equals(possiblyMockLeaderboard)) {
            log.info("[DEBUG] - mockLeaderboard: {}", mockLeaderboard.toString());
            log.info("[DEBUG] - possiblyMockLeaderboard: {}", possiblyMockLeaderboard.toString());
            fail("most recent leaderboard metadata did not return mock leaderboard");
        }
    }

    @Order(2)
    @Test
    void testGetLeaderboardMetadataById() {
        Leaderboard possiblyMockLeaderboard = leaderboardRepository.getLeaderboardMetadataById(mockLeaderboard.getId());

        if (possiblyMockLeaderboard == null || !mockLeaderboard.equals(possiblyMockLeaderboard)) {
            log.info("[DEBUG] - mockLeaderboard: {}", mockLeaderboard.toString());
            log.info("[DEBUG] - possiblyMockLeaderboard: {}", possiblyMockLeaderboard.toString());
            fail("most recent leaderboard metadata did not return mock leaderboard");
        }
    }

    @Order(3)
    @Test
    void testUpdateLeaderboardName() {
        mockLeaderboard.setName("New test name");
        leaderboardRepository.updateLeaderboard(mockLeaderboard);

        // ensure that they are still equal

        Leaderboard possiblyMockLeaderboard = leaderboardRepository.getLeaderboardMetadataById(mockLeaderboard.getId());

        if (possiblyMockLeaderboard == null || !mockLeaderboard.equals(possiblyMockLeaderboard)) {
            log.info("[DEBUG] - mockLeaderboard: {}", mockLeaderboard.toString());
            log.info("[DEBUG] - possiblyMockLeaderboard: {}", possiblyMockLeaderboard.toString());
            fail("most recent leaderboard metadata did not return mock leaderboard");
        }
    }

    @Order(4)
    @Test
    void testAddAllUsersToMockLeaderboard() {
        boolean isSuccessful = leaderboardRepository.addAllUsersToLeaderboard(mockLeaderboard.getId());

        assertTrue(isSuccessful, "failed to add all users to mock leaderboard");
    }

    @Order(5)
    @Test
    void testGetRecentLeaderboardUserCount() {
        int leaderboardUsersCount = leaderboardRepository.getRecentLeaderboardUserCount(false, "");
        assertTrue(leaderboardUsersCount > 0);
    }

    @Order(6)
    @Test
    void testLeaderboardUsersByIdEqualsRecentLeaderboardUsers() {
        List<UserWithScore> allLeaderboardUsersById = leaderboardRepository.getLeaderboardUsersById(mockLeaderboard.getId(), 1, PAGE_SIZE, "", false);
        List<UserWithScore> allRecentLeaderboardUsers = leaderboardRepository.getRecentLeaderboardUsers(1, PAGE_SIZE, "", false);

        IntStream.range(0, allLeaderboardUsersById.size())
                        .forEach(i -> {
                            var user = allLeaderboardUsersById.get(i);
                            assertTrue(allRecentLeaderboardUsers.stream()
                                            .anyMatch(leaderboardUser -> {
                                                return leaderboardUser.getId().equals(user.getId());
                                            }));
                        });
    }

    @Order(7)
    @Test
    void testUsersInMockLeaderboardToAllUsers() {
        List<User> allUsers = userRepository.getAllUsers();
        List<UserWithScore> allLeaderboardUsers = leaderboardRepository.getLeaderboardUsersById(mockLeaderboard.getId(), 1, allUsers.size(), "", false);

        assertTrue(allUsers != null);
        assertTrue(allLeaderboardUsers != null);
        assertTrue(allUsers.size() > 0);
        assertTrue(allLeaderboardUsers.size() > 0);
        assertTrue(allUsers.size() == allLeaderboardUsers.size());

        IntStream.range(0, allUsers.size())
                        .forEach(i -> {
                            var user = allUsers.get(i);

                            assertTrue(allLeaderboardUsers.stream()
                                            .anyMatch(leaderboardUser -> {
                                                return leaderboardUser.getId().equals(user.getId());
                                            }));
                        });
    }

    @Order(8)
    @Test
    void testAddingANewUserToMockLeaderboard() {
        User newMockUser = new User("3021234402183490", "mockUserName" + ThreadLocalRandom.current().nextInt());
        userRepository.createNewUser(newMockUser);

        boolean isAddUserSuccessful = leaderboardRepository.addUserToLeaderboard(newMockUser.getId(), mockLeaderboard.getId());

        assertTrue(isAddUserSuccessful, "failed to add new user to mock leaderboard");

        // cleanup
        boolean isDeleteUserSuccessful = userRepository.deleteUserById(newMockUser.getId());

        assertTrue(isDeleteUserSuccessful, "failed to delete new user");

        // leaderboard should be purged of user via cascade deleting
    }

    @Order(9)
    @Test
    void testGivingPointsToSuperUserOnMockLeaderboard() {
        int newPoints = 99999;
        boolean isUserUpdatePointsSuccessful = leaderboardRepository.updateUserPointsFromLeaderboard(mockLeaderboard.getId(), superUserId, newPoints);

        assertTrue(isUserUpdatePointsSuccessful, "failed to update points of super user on mock leaderboard");

        UserWithScore superUser = userRepository.getUserWithScoreById(superUserId, mockLeaderboard.getId());

        assertTrue(superUser != null);
        assertEquals(newPoints, superUser.getTotalScore());
    }

    @Test
    void testLeaderboardUserCountById() {
        int leaderboardUsersCount = leaderboardRepository.getLeaderboardUserCountById(mockLeaderboard.getId(), false, "");
        assertTrue(leaderboardUsersCount > 0);
    }

    @Test
    void testLeaderboardCount() {
        int leaderboardCount = leaderboardRepository.getLeaderboardCount();
        assertTrue(leaderboardCount > 0);
    }

    @Test
    void testGetAllLeaderboards() {
        var leaderboards = leaderboardRepository.getAllLeaderboardsShallow(1, 9999999, "");
        assertTrue(leaderboards != null);
        assertTrue(leaderboards.size() > 0);
    }
}
