package com.patina.codebloom.common.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "secret")
@Getter
@Setter
public class JWTProperties {
    private String key;
}
