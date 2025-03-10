package com.patina.codebloom.website.leetcode.client.score;

import java.util.concurrent.ThreadLocalRandom;

import com.patina.codebloom.website.leetcode.model.QuestionDifficulty;

public class ScoreCalculator {
    // TODO - Discuss the final values.
    private static final int EASY_MULTIPLIER = 100;
    private static final int MEDIUM_MULTIPLIER = 300;
    private static final int HARD_MULTIPLIER = 600;

    public static int calculateScore(final QuestionDifficulty questionDifficulty, final float acceptanceRate, final float multiplier) {
        int baseScore;
        switch (questionDifficulty) {
        case Easy:
            baseScore = (int) Math.floor(EASY_MULTIPLIER * (1.0 - acceptanceRate));
            break;
        case Medium:
            baseScore = (int) Math.floor(MEDIUM_MULTIPLIER * (1.0 - acceptanceRate));
            break;
        case Hard:
            baseScore = (int) Math.floor(HARD_MULTIPLIER * (1.0 - acceptanceRate));
            break;
        default:
            return 0;
        }

        // Apply a 5% deviation
        double deviation = ThreadLocalRandom.current().nextDouble(0.95, 1.05);
        return (int) Math.floor(baseScore * deviation * multiplier);
    }

    public static float calculateMultiplier(final QuestionDifficulty questionDifficulty) {
        float baseMultiplier;
        switch (questionDifficulty) {
        case Easy:
            baseMultiplier = 1.3f;
            break;
        case Medium:
            baseMultiplier = 1.5f;
            break;
        case Hard:
            baseMultiplier = 1.8f;
            break;
        default:
            return 1.0f;
        }

        float deviation = ThreadLocalRandom.current().nextFloat(0.95f, 1.05f);
        return Math.round(baseMultiplier * deviation * 100.0f) / 100.0f;
    }
}
