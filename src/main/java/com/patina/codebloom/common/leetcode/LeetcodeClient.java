package com.patina.codebloom.common.leetcode;

import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import java.util.List;
import java.util.Set;

public interface LeetcodeClient {
    LeetcodeQuestion findQuestionBySlug(String slug);

    /**
     * Default method that doesn't require a limit. The default limit will return 20 submissions.
     *
     * @param username
     */
    List<LeetcodeSubmission> findSubmissionsByUsername(String username);

    /**
     * This method specifies a submissions limit to adhere to.
     *
     * @param username
     * @param limit
     */
    List<LeetcodeSubmission> findSubmissionsByUsername(String username, int limit);

    /** @implNote requires authentication */
    LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId);

    POTD getPotd();

    UserProfile getUserProfile(String username);

    Set<LeetcodeTopicTag> getAllTopicTags();

    List<LeetcodeQuestion> getAllProblems();
}
