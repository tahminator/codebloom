package com.patina.codebloom.db.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;
import com.patina.codebloom.common.db.repos.usertag.UserTagRepository;
import com.patina.codebloom.common.db.repos.usertag.options.UserTagFilterOptions;
import com.patina.codebloom.db.BaseRepositoryTest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UserTagRepositoryTest extends BaseRepositoryTest {

    private UserTagRepository userTagRepository;
    private UserTag testUserTag;
    private UserTag deletableUserTag;
    private String mockUserId = "dba9600f-c762-4378-8b9a-94015c0121e1";
    private String mockUserId2 = "4013ce85-0d5c-44d6-9975-118266489a96";

    @Autowired
    public UserTagRepositoryTest(final UserTagRepository userTagRepository) {
        this.userTagRepository = userTagRepository;
    }

    @BeforeAll
    void createUserTag() {
        testUserTag = UserTag.builder().userId(mockUserId).tag(Tag.Patina).build();

        userTagRepository.createTag(testUserTag);
    }

    @AfterAll
    void cleanUp() {
        boolean isSuccessful = userTagRepository.deleteTagByTagId(testUserTag.getId());

        if (!isSuccessful) {
            fail("Failed deleting Tag by TagId.");
        }
    }

    @Test
    @Order(1)
    void testGetId() {
        UserTag found = userTagRepository.findTagByTagId(testUserTag.getId());
        assertNotNull(found);
        assertEquals(testUserTag.getId(), found.getId());
    }

    @Test
    @Order(2)
    void testFindAllTagsByUserIdWithPointOfTime() {
        List<UserTag> tagList = userTagRepository.findTagsByUserId(
                mockUserId,
                UserTagFilterOptions.builder().pointOfTime(OffsetDateTime.MIN).build());
        assertNotNull(tagList);
        assertEquals(0, tagList.size());
    }

    @Test
    @Order(3)
    void testFindAllTagsByUserId() {
        ArrayList<UserTag> tagList = userTagRepository.findTagsByUserId(mockUserId);
        assertNotNull(tagList);
        assertFalse(tagList.isEmpty());
        assertTrue(tagList.stream().anyMatch(tag -> tag.getId().equals(testUserTag.getId())));
    }

    @Test
    @Order(4)
    void testDeleteTagByUserIdAndTag() {
        deletableUserTag = UserTag.builder().userId(mockUserId2).tag(Tag.Patina).build();

        userTagRepository.createTag(deletableUserTag);

        UserTag found = userTagRepository.findTagByTagId(deletableUserTag.getId());
        assertNotNull(found);
        assertEquals(deletableUserTag.getId(), found.getId());

        boolean deleted = userTagRepository.deleteTagByUserIdAndTag(mockUserId2, deletableUserTag.getTag());
        assertTrue(deleted);
    }
}
