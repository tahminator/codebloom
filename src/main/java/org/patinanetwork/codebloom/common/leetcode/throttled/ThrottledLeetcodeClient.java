package org.patinanetwork.codebloom.common.leetcode.throttled;

import org.patinanetwork.codebloom.common.leetcode.LeetcodeClient;

/** Attaches a rate limiter over {@link LeetcodeClient} to avoid getting rate-limited from leetcode.com */
public interface ThrottledLeetcodeClient extends LeetcodeClient {}
