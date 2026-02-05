package org.patinanetwork.codebloom.common.redis;

import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.SetParams;

@Component
@Slf4j
public class RedisClient {

    private final UnifiedJedis client;

    public RedisClient(JedisClientManager clientManager) {
        client = clientManager.getClient();
    }

    private void setAuth(final String auth, final long expires) {
        client.set("auth", auth, SetParams.setParams().ex(expires));
    }

    /** Get auth token. */
    public Optional<String> getAuth() {
        return Optional.ofNullable(client.get("auth"));
    }

    /** Set auth token and when it should be ejected from the cache. */
    public void setAuth(final String auth, final long units, final ChronoUnit chronoUnit) {
        setAuth(auth, chronoUnit.getDuration().multipliedBy(units).toSeconds());
    }
}
