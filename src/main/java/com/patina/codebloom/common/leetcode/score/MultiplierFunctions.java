package com.patina.codebloom.common.leetcode.score;

/*
 Link to the functions on desmos:
 https://www.desmos.com/calculator/snbtjxxsbt
 */
public class MultiplierFunctions {
    public static float purpleFunction(final float acceptanceRate) {
        return 1.0f - acceptanceRate / 4;
    }

    public static float orangeFunction(final float acceptanceRate) {
        return 1.0f / (acceptanceRate + 1);
    }

    public static float blueFunction(final float acceptanceRate) {
        return (float) (1.0 / (2 * Math.pow(acceptanceRate + 1, 2)) + 0.5);
    }

}