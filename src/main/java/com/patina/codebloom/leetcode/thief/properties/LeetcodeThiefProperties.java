package com.patina.codebloom.leetcode.thief.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "thief")
@Getter
@Setter
public class LeetcodeThiefProperties {
    private Strategy strategy;

    private StrategyProperties github;
    private StrategyProperties facebook;

    @Getter
    @Setter
    public static class EmailProperties {
        private String host;
        private int port;
        private String type;
        private String username;
        private String password;
    }

    @Getter
    @Setter
    public static class StrategyProperties {
        private String username;
        private String password;
        private EmailProperties email;
    }

    public enum Strategy {
        FACEBOOK,
        GITHUB
    }
}
