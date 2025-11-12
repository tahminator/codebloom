package com.patina.codebloom.leetcode.client.throttled;

import com.patina.codebloom.leetcode.client.LeetcodeClient;

/**
 * Attaches a rate limiter over {@link LeetcodeClient} to avoid getting
 * rate-limited from leetcode.com
 */
public interface ThrottledLeetcodeClient extends LeetcodeClient {
}
