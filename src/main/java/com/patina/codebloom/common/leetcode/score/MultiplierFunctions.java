package com.patina.codebloom.common.leetcode.score;

public class MultiplierFunctions {
    public static float purpleFunction(float acceptanceRate) {
        return 1.0f - acceptanceRate / 400;
    }

    public static float orangeFunction(float acceptanceRate) {
        return 1.0f / (acceptanceRate / 100 + 1);
    }

    public static float greenFunction(float acceptanceRate) {
        return (float) (1.0 / (2 * Math.pow(acceptanceRate / 100 + 1, 2)) + 0.5);
    }
}