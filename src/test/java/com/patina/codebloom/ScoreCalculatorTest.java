package com.patina.codebloom;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Easy;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Hard;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Medium;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;

class ScoreCalculatorTest {
    @Test
    void easyScore() {
        int finalScore = ScoreCalculator.calculateScore(Easy, 0.0f, 5.0f);
        assertEquals(true, finalScore >= 475 && finalScore <= 525);
    }

    @Test
    void mediumScore() {
        int finalScore = ScoreCalculator.calculateScore(Medium, 0.0f, 5.0f);
        assertEquals(true, finalScore >= 1425 && finalScore <= 1575);
    }

    @Test
    void hardScore() {
        int finalScore = ScoreCalculator.calculateScore(Hard, 0.0f, 5.0f);
        assertEquals(true, finalScore >= 2850 && finalScore <= 3150);
    }

    @Test
    void easyMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Easy);
        assertEquals(true, baseMultiplier >= 1.3f * 0.95f && baseMultiplier <= 1.3f * 1.05f);
    }

    @Test
    void mediumMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Medium);
        assertEquals(true, baseMultiplier >= 1.5f * 0.95f && baseMultiplier <= 1.5f * 1.05f);
    }

    @Test
    void hardMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Hard);
        assertEquals(true, baseMultiplier >= 1.8f * 0.95f && baseMultiplier <= 1.8f * 1.05f);
    }

}
