package com.patina.codebloom.common.leetcode;

import java.util.List;
import java.util.Set;

import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;

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
