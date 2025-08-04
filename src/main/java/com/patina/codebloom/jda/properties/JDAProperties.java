package com.patina.codebloom.jda.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "jda.discord")
@AllArgsConstructor
@Getter
public class JDAProperties {
    private String token;
}
