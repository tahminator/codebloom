package com.patina.codebloom.common.jedis;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

import lombok.Getter;
import lombok.Setter;

@Profile("ci")
@ConfigurationProperties(prefix = "jedis")
@Getter
@Setter
public class JedisClientConfiguration {
    private String url;
}
