package com.patina.codebloom.common.leetcode.score;

import com.patina.codebloom.common.db.models.question.QuestionDifficulty;
import java.util.concurrent.ThreadLocalRandom;

public class ScoreCalculator {

    private static final int EASY_MULTIPLIER = 100;
    private static final int MEDIUM_MULTIPLIER = 300;
    private static final int HARD_MULTIPLIER = 600;

    public static int calculateScore(
            final QuestionDifficulty questionDifficulty, final float acceptanceRate, final float multiplier) {
        int baseScore =
                switch (questionDifficulty) {
                    case Easy -> (int) Math.floor(EASY_MULTIPLIER * MultiplierFunctions.purpleFunction(acceptanceRate));
                    case Medium ->
                        (int) Math.floor(MEDIUM_MULTIPLIER * MultiplierFunctions.blueFunction(acceptanceRate));
                    case Hard -> (int) Math.floor(HARD_MULTIPLIER * MultiplierFunctions.blueFunction(acceptanceRate));
                    default -> 0;
                };

        // Apply a 5% deviation
        double deviation = ThreadLocalRandom.current().nextDouble(0.95, 1.05);
        return (int) Math.floor(baseScore * deviation * multiplier);
    }

    public static float calculateMultiplier(final QuestionDifficulty questionDifficulty) {
        float baseMultiplier =
                switch (questionDifficulty) {
                    case Easy -> 1.3f;
                    case Medium -> 1.5f;
                    case Hard -> 1.8f;
                    default -> 1.0f;
                };

        float deviation = ThreadLocalRandom.current().nextFloat(0.95f, 1.05f);
        return Math.round(baseMultiplier * deviation * 100.0f) / 100.0f;
    }
}
