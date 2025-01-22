package com.patina.codebloom.common.leetcode.score;

import java.util.concurrent.ThreadLocalRandom;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;

public class ScoreCalculator {
    // TODO - Discuss the final values.
    private final static int EASY_MULTIPLIER = 100;
    private final static int MEDIUM_MULTIPLIER = 300;
    private final static int HARD_MULTIPLIER = 600;

    public static int calculateScore(QuestionDifficulty questionDifficulty, float acceptanceRate) {
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
        return (int) Math.floor(baseScore * deviation);
    }
}
