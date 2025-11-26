package com.patina.codebloom.common.jedis;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;

@Profile("ci")
@ConfigurationProperties(prefix = "jedis")
@Getter
@Setter
public class JedisClientConfiguration {

    private String url;
}
