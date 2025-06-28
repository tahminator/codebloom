package com.patina.codebloom.common.db.repos.usertag;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;

public interface UserTagRepository {
    UserTag findTagByTagId(String tagId);

    UserTag findTagByUserIdAndTag(String userId, Tag tag);

    ArrayList<UserTag> findTagsByUserId(String userId);

    /**
     * @note - The provided object's methods will be overridden with any returned
     * data from the database.
     * 
     * @param userTag - required fields:
     * <ul>
     * <li>userId</li>
     * <li>tag</li>
     * </ul>
     */
    void createTag(UserTag userTag);

    boolean deleteTagByTagId(String tagId);

    boolean deleteTagByUserIdAndTag(String userId, Tag tag);
}
