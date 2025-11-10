package com.patina.codebloom.leetcode;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Easy;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Hard;
import static com.patina.codebloom.common.db.models.question.QuestionDifficulty.Medium;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;

class ScoreCalculatorTest {
    @Test
    void easyScore() {
        int finalScore = ScoreCalculator.calculateScore(Easy, 0.5f, 5.0f);
        assertEquals(true, finalScore >= 415 && finalScore <= 459);
    }

    @Test
    void mediumScore() {
        int finalScore = ScoreCalculator.calculateScore(Medium, 0.5f, 5.0f);
        assertEquals(true, finalScore >= 1029 && finalScore <= 1137);
    }

    @Test
    void hardScore() {
        int finalScore = ScoreCalculator.calculateScore(Hard, 0.5f, 5.0f);
        assertEquals(true, finalScore >= 2058 && finalScore <= 2275);
    }

    @Test
    void easyMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Easy);
        assertEquals(true, baseMultiplier >= 1.235f && baseMultiplier <= 1.365f);
    }

    @Test
    void mediumMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Medium);
        assertEquals(true, baseMultiplier >= 1.425f && baseMultiplier <= 1.575f);
    }

    @Test
    void hardMultiplier() {
        float baseMultiplier = ScoreCalculator.calculateMultiplier(Hard);
        assertEquals(true, baseMultiplier >= 1.71f && baseMultiplier <= 1.89f);
    }
}
