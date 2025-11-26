package com.patina.codebloom.leetcode;

import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Easy;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Hard;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Medium;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.patina.codebloom.common.leetcode.score.ScoreCalculator;
import org.junit.jupiter.api.Test;

class ScoreCalculatorTest {

    @Test
    void easyScore() {
        for (int i = 0; i < 1000; i++) {
            int finalScore = ScoreCalculator.calculateScore(Easy, 0.5f, 5.0f);
            assertEquals(true, finalScore >= 412 && finalScore <= 462);
        }
    }

    @Test
    void mediumScore() {
        for (int i = 0; i < 1000; i++) {
            int finalScore = ScoreCalculator.calculateScore(Medium, 0.5f, 5.0f);
            assertEquals(true, finalScore >= 1026 && finalScore <= 1140);
        }
    }

    @Test
    void hardScore() {
        for (int i = 0; i < 1000; i++) {
            int finalScore = ScoreCalculator.calculateScore(Hard, 0.5f, 5.0f);
            assertEquals(true, finalScore >= 2055 && finalScore <= 2278);
        }
    }

    @Test
    void easyMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Easy);
        assertEquals(
            true,
            baseMultiplier >= 1.235f && baseMultiplier <= 1.365f
        );
    }

    @Test
    void mediumMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Medium);
        assertEquals(
            true,
            baseMultiplier >= 1.425f && baseMultiplier <= 1.575f
        );
    }

    @Test
    void hardMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Hard);
        assertEquals(true, baseMultiplier >= 1.71f && baseMultiplier <= 1.89f);
    }
}
