package com.patina.codebloom.common.db.repos.usertag;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.models.usertag.UserTag;

public interface UserTagRepository {
    UserTag findTagByTagId(String tagId);

    UserTag findTagByUserIdAndTag(String userId, Tag tag);

    ArrayList<UserTag> findTagsByUserId(String userId);

    /**
     * Returns the id of the newly created tag.
     */
    String createTagByUserId(String userId, Tag tag);

    boolean deleteTagByTagId(String tagId);

    boolean deleteTagByUserIdAndTag(String userId, Tag tag);
}
