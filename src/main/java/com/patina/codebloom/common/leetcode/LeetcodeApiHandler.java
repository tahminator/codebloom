package com.patina.codebloom.common.leetcode;

import java.util.ArrayList;

import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;

public interface LeetcodeApiHandler {

    LeetcodeQuestion findQuestionBySlug(String slug);

    ArrayList<LeetcodeSubmission> findSubmissionsByUsername(String username);

    LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId);

    POTD getPotd();
}
