package com.patina.codebloom.common.leetcode;

import java.util.ArrayList;

import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;

public interface LeetcodeApiHandler {

    public LeetcodeQuestion findQuestionBySlug(String slug);

    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(String username);

    public LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId);

    public POTD getPotd();
}
