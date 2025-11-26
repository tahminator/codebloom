package com.patina.codebloom.common.jedis;

import com.patina.codebloom.common.env.Env;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.params.SetParams;

/**
 * <b>The client is only loaded in CI.</b> That may change at a later date.
 * Check wiki for details.
 */
@Component
@Slf4j
@EnableConfigurationProperties({ JedisClientConfiguration.class })
public class JedisClient {

    private final JedisClientConfiguration jedisClientConfiguration;

    private UnifiedJedis client;
    private Env env;

    public JedisClient(
        final JedisClientConfiguration jedisClientConfiguration,
        final Env env
    ) {
        this.jedisClientConfiguration = jedisClientConfiguration;
        this.env = env;
    }

    @PostConstruct
    private void open() {
        if (!env.isCi()) {
            return;
        }

        if (client == null) {
            client = new UnifiedJedis(jedisClientConfiguration.getUrl());
        }
    }

    @PreDestroy
    private void close() {
        if (client != null) {
            client.close();
        }
    }

    /**
     * Get auth token. Will return empty {@link Optional} if not in CI.
     */
    public Optional<String> getAuth() {
        if (!env.isCi()) {
            return Optional.empty();
        }

        return Optional.ofNullable(client.get("auth"));
    }

    /**
     * Set auth token and when it should be ejected from the cache. If not in CI,
     * will register an error message, but will no-op.
     */
    public void setAuth(final String auth, final long expires) {
        if (!env.isCi()) {
            log.error(
                "You called JedisClient.setAuth in a non-CI environment. As a result, this operation has failed, but will not error."
            );
            return;
        }

        client.set("auth", auth, SetParams.setParams().ex(expires));
    }

    /**
     * Set auth token and when it should be ejected from the cache. If not in CI,
     * will register an error message, but will no-op.
     */
    public void setAuth(
        final String auth,
        final long units,
        final ChronoUnit chronoUnit
    ) {
        setAuth(auth, chronoUnit.getDuration().multipliedBy(units).toSeconds());
    }
}
