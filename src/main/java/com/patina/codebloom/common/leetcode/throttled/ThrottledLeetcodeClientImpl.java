package com.patina.codebloom.common.leetcode.throttled;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.patina.codebloom.common.leetcode.LeetcodeClientImpl;
import com.patina.codebloom.common.leetcode.models.LeetcodeDetailedQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeQuestion;
import com.patina.codebloom.common.leetcode.models.LeetcodeSubmission;
import com.patina.codebloom.common.leetcode.models.LeetcodeTopicTag;
import com.patina.codebloom.common.leetcode.models.POTD;
import com.patina.codebloom.common.leetcode.models.UserProfile;
import com.patina.codebloom.scheduled.auth.LeetcodeAuthStealer;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.BlockingBucket;
import io.github.bucket4j.Bucket;

@Component
public class ThrottledLeetcodeClientImpl extends LeetcodeClientImpl implements ThrottledLeetcodeClient {
    private static final long REQUESTS_OVER_TIME = 1L;
    private static final long MILLISECONDS_TO_WAIT = 50L;
    private final BlockingBucket rateLimiter;

    private BlockingBucket initializeBucket() {
        var bandwidth = Bandwidth.builder()
                        .capacity(REQUESTS_OVER_TIME)
                        .refillIntervally(REQUESTS_OVER_TIME, Duration.ofMillis(MILLISECONDS_TO_WAIT))
                        .build();

        return Bucket.builder()
                        .addLimit(bandwidth)
                        .build().asBlocking();
    }

    private void waitForToken() {
        try {
            rateLimiter.consume(1);
        } catch (InterruptedException e) {
            throw new RuntimeException("Failed to consume rate limit bucket token in leetcode client", e);
        }
    }

    public ThrottledLeetcodeClientImpl(final LeetcodeAuthStealer leetcodeAuthStealer) {
        super(leetcodeAuthStealer);
        this.rateLimiter = initializeBucket();
    }

    @Override
    public LeetcodeQuestion findQuestionBySlug(final String slug) {
        waitForToken();
        return super.findQuestionBySlug(slug);
    }

    @Override
    public ArrayList<LeetcodeSubmission> findSubmissionsByUsername(final String username) {
        waitForToken();
        return super.findSubmissionsByUsername(username);
    }

    @Override
    public LeetcodeDetailedQuestion findSubmissionDetailBySubmissionId(final int submissionId) {
        waitForToken();
        return super.findSubmissionDetailBySubmissionId(submissionId);
    }

    @Override
    public POTD getPotd() {
        waitForToken();
        return super.getPotd();
    }

    @Override
    public UserProfile getUserProfile(final String username) {
        waitForToken();
        return super.getUserProfile(username);
    }

    @Override
    public Set<LeetcodeTopicTag> getAllTopicTags() {
        rateLimiter.acquire();
        return super.getAllTopicTags();
    }
}
