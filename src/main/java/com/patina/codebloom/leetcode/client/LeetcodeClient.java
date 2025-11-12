package com.patina.codebloom.leetcode.client;

import java.util.List;
import java.util.Set;

import com.patina.codebloom.leetcode.client.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.leetcode.client.models.LeetcodeQuestion;
import com.patina.codebloom.leetcode.client.models.LeetcodeSubmission;
import com.patina.codebloom.leetcode.client.models.LeetcodeTopicTag;
import com.patina.codebloom.leetcode.client.models.POTD;
import com.patina.codebloom.leetcode.client.models.UserProfile;

public interface LeetcodeClient {

    LeetcodeQuestion findQuestionBySlug(String slug);

    List<LeetcodeSubmission> findSubmissionsByUsername(String username);

    /**
     * @implNote requires authentication
     */
    LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId);

    POTD getPotd();

    UserProfile getUserProfile(String username);

    Set<LeetcodeTopicTag> getAllTopicTags();
}
