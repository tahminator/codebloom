package com.patina.codebloom.website.leetcode.client;

import java.util.ArrayList;

import com.patina.codebloom.website.leetcode.client.model.LeetcodeDetailedQuestion;
import com.patina.codebloom.website.leetcode.client.model.LeetcodePOTD;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeQuestion;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeSubmission;
import com.patina.codebloom.website.leetcode.client.model.LeetcodeUserProfile;

/**
 * This client is used to interface with leetcode.com
 */
public interface LeetcodeApiClient {

    LeetcodeQuestion findQuestionBySlug(String slug);

    ArrayList<LeetcodeSubmission> findSubmissionsByUsername(String username);

    LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(int submissionId);

    LeetcodePOTD getPotd();

    LeetcodeUserProfile getUserProfile(String username);
}
