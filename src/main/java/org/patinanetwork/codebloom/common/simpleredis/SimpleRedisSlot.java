package org.patinanetwork.codebloom.common.simpleredis;

import io.github.bucket4j.Bucket;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class SimpleRedisSlot<V> {
    private final int index;
    private final Class<V> clazz;

    public static final SimpleRedisSlot<Long> SUBMISSION_REFRESH = new SimpleRedisSlot<>(0, Long.class);
    public static final SimpleRedisSlot<Long> VERIFICATION_EMAIL_SENDING = new SimpleRedisSlot<>(1, Long.class);
    public static final SimpleRedisSlot<Bucket> GLOBAL_RATE_LIMIT = new SimpleRedisSlot<>(2, Bucket.class);
}
