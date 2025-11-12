package com.patina.codebloom.leetcode.thief;

import java.util.concurrent.CompletableFuture;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.Async;

import com.patina.codebloom.leetcode.thief.properties.LeetcodeThiefProperties;

import lombok.extern.slf4j.Slf4j;

@EnableConfigurationProperties({ LeetcodeThiefProperties.class })
@Slf4j
public class LeetcodeThief {
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; U; Android 4.4.1; SAMSUNG SM-J210G Build/KTU84P) AppleWebKit/536.31 (KHTML, like Gecko) Chrome/48.0.2090.359 Mobile Safari/601.9";

    private final LeetcodeThiefProperties.Strategy strategy;
    private final LeetcodeThiefProperties.StrategyProperties props;

    public LeetcodeThief(final LeetcodeThiefProperties leetcodeThiefProperties) {
        strategy = leetcodeThiefProperties.getStrategy();

        this.props = switch (strategy) {
            case GITHUB -> leetcodeThiefProperties.getGithub();
            case FACEBOOK -> leetcodeThiefProperties.getFacebook();
            default -> throw new RuntimeException("Invalid thief strategy");
        };
    }

    @Async
    public CompletableFuture<String> loadCookie() {
        return CompletableFuture.completedFuture(null);
    };
}
