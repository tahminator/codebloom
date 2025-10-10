package com.patina.codebloom.db.user;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.UUID;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.patina.codebloom.common.db.models.user.User;
import com.patina.codebloom.common.db.repos.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(OrderAnnotation.class)
@Slf4j
public class UserRepositoryTest {
    private UserRepository userRepository;

    private User testUser;

    @Autowired
    public UserRepositoryTest(final UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @BeforeAll
    void setUp() {
        String uniqueDiscordId = "test-" + System.currentTimeMillis();

        testUser = User.builder()
                        .discordId(uniqueDiscordId)
                        .discordName("TestUser")
                        .leetcodeUsername("testuser")
                        .nickname("TestNickname")
                        .admin(false)
                        .schoolEmail("test@example.com")
                        .profileUrl("")
                        .tags(new ArrayList<>())
                        .build();

        userRepository.createUser(testUser);
        userRepository.updateUser(testUser);
    }

    @AfterAll
    void cleanUp() {
        User existingUser = userRepository.getUserById(testUser.getId());
        if (existingUser != null) {
            boolean isSuccessful = userRepository.deleteUserById(testUser.getId());
            if (!isSuccessful) {
                fail("Failed deleting User by Id.");
            }
        }
    }

    @Test
    @Order(1)
    void testGetId() {
        User found = userRepository.getUserById(testUser.getId());
        assertNotNull(found);
        assertEquals(testUser, found);
    }

    @Test
    @Order(2)
    void testGetUserByDiscordId() {
        User found = userRepository.getUserByDiscordId(testUser.getDiscordId());
        assertNotNull(found);
        assertEquals(testUser, found);
    }

    @Test
    @Order(3)
    void testGetUserByLeetcodeUsername() {
        User found = userRepository.getUserByLeetcodeUsername(testUser.getLeetcodeUsername());
        assertNotNull(found);
        assertEquals(testUser, found);
    }

    @Test
    @Order(4)
    void testUpdateUser() {
        String newNickname = "Updated Nickname";
        testUser.setNickname(newNickname);

        boolean updateResult = userRepository.updateUser(testUser);
        assertTrue(updateResult);

        User updatedUser = userRepository.getUserById(testUser.getId());
        assertNotNull(updatedUser);
        assertEquals(newNickname, updatedUser.getNickname());
    }

    @Test
    @Order(5)
    void testGetUserCount() {
        int count = userRepository.getUserCount();
        assertTrue(count > 0);
    }

    @Test
    @Order(6)
    void testGetUserCountWithQuery() {
        int countWithTestUser = userRepository.getUserCount("TestUser");
        assertTrue(countWithTestUser > 0);
    }

    @Test
    @Order(7)
    void testGetAllUsers() {
        ArrayList<User> users = userRepository.getAllUsers();
        assertNotNull(users);
        assertTrue(users.size() > 0);
        assertTrue(users.stream().anyMatch(user -> user.getId().equals(testUser.getId())));
    }

    @Test
    @Order(8)
    void testGetAllUsersWithPagination() {
        ArrayList<User> users = userRepository.getAllUsers(1, 5, "");
        assertNotNull(users);
        assertTrue(users.size() >= 0);
        ArrayList<User> searchResults = userRepository.getAllUsers(1, 100, "TestUser");
        assertTrue(searchResults.stream().anyMatch(user -> user.getId().equals(testUser.getId())));
    }

    @Test
    @Order(9)
    void testUserExistsByLeetcodeUsername() {
        boolean exists = userRepository.userExistsByLeetcodeUsername(testUser.getLeetcodeUsername());
        assertTrue(exists);
    }

    @Test
    @Order(10)
    void testDeleteUserById() {
        String uniqueDiscordId = "deletable-" + System.currentTimeMillis();

        User deletableUser = User.builder()
                        .discordId(uniqueDiscordId)
                        .discordName("DeletableUser")
                        .admin(false)
                        .build();

        userRepository.createUser(deletableUser);

        User found = userRepository.getUserById(deletableUser.getId());
        assertNotNull(found);
        assertEquals(deletableUser.getId(), found.getId());

        boolean deleted = userRepository.deleteUserById(deletableUser.getId());
        assertTrue(deleted);

        User deletedUser = userRepository.getUserById(deletableUser.getId());
        assertNull(deletedUser);
    }

}