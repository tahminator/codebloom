package org.patinanetwork.codebloom.common.leetcode;

public class LeetcodeClientException extends RuntimeException {

    public LeetcodeClientException(final String message) {
        super(message);
    }

    public LeetcodeClientException(final String message, final Throwable e) {
        super(message, e);
    }
}
