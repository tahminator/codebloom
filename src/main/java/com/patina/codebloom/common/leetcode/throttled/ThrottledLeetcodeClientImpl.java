package com.patina.codebloom.common.leetcode.throttled;

import java.util.ArrayList;

import org.springframework.stereotype.Component;

import com.google.common.util.concurrent.RateLimiter;
import com.patina.codebloom.common.leetcode.LeetcodeClientImpl;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

@Component
public class ThrottledLeetcodeClientImpl extends LeetcodeClientImpl implements ThrottledLeetcodeClient {
    private static final double REQUESTS_PER_SECOND = 40.0d;
    private final RateLimiter rateLimiter;

    public ThrottledLeetcodeClientImpl(final LeetcodeAuthStealer leetcodeAuthStealer) {
        super(leetcodeAuthStealer);
        this.rateLimiter = RateLimiter.create(REQUESTS_PER_SECOND);
    }

    @Override
    public LeetcodeQuestion findQuestionBySlug(final String slug) {
        rateLimiter.acquire();
        return super.findQuestionBySlug(slug);
    }

    @Override
    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(final String username) {
        rateLimiter.acquire();
        return super.findSubmissionsByUsername(username);
    }

    @Override
    public LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(final int submissionId) {
        rateLimiter.acquire();
        return super.findSubmissionDetailBySubmissionId(submissionId);
    }

    @Override
    public POTD getPotd() {
        rateLimiter.acquire();
        return super.getPotd();
    }

    @Override
    public UserProfile getUserProfile(final String username) {
        rateLimiter.acquire();
        return super.getUserProfile(username);
    }
}
