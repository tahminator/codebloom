package org.patinanetwork.codebloom.common.jedis;

public class JedisClientNotSupportedException extends RuntimeException {

    public JedisClientNotSupportedException(final String message) {
        super(message);
    }
}
