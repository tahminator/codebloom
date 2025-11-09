package com.patina.codebloom.leetcode;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import com.patina.codebloom.common.leetcode.score.MultiplierFunctions;

public class MultiplierFunctionsTest {
    @Test
    void testPurpleFunction() {
        int baseEasy = 100;
        assertEquals(93, (int) (baseEasy * MultiplierFunctions.purpleFunction(0.25f)));
        assertEquals(87, (int) (baseEasy * MultiplierFunctions.purpleFunction(0.5f)));
        assertEquals(81, (int) (baseEasy * MultiplierFunctions.purpleFunction(0.75f)));
    }

    @Test
    void testOrangeFunction() {
        int baseMedium = 300;
        int baseHard = 600;
        assertEquals(240, (int) (baseMedium * MultiplierFunctions.orangeFunction(0.25f)));
        assertEquals(200, (int) (baseMedium * MultiplierFunctions.orangeFunction(0.5f)));
        assertEquals(171, (int) (baseMedium * MultiplierFunctions.orangeFunction(0.75f)));

        assertEquals(480, (int) (baseHard * MultiplierFunctions.orangeFunction(0.25f)));
        assertEquals(400, (int) (baseHard * MultiplierFunctions.orangeFunction(0.5f)));
        assertEquals(342, (int) (baseHard * MultiplierFunctions.orangeFunction(0.75f)));
    }

    @Test
    void testBlueFunction() {
        int baseMedium = 300;
        int baseHard = 600;
        assertEquals(246, (int) (baseMedium * MultiplierFunctions.blueFunction(0.25f)));
        assertEquals(216, (int) (baseMedium * MultiplierFunctions.blueFunction(0.5f)));
        assertEquals(198, (int) (baseMedium * MultiplierFunctions.blueFunction(0.75f)));

        assertEquals(492, (int) (baseHard * MultiplierFunctions.blueFunction(0.25f)));
        assertEquals(433, (int) (baseHard * MultiplierFunctions.blueFunction(0.5f)));
        assertEquals(397, (int) (baseHard * MultiplierFunctions.blueFunction(0.75f)));
    }
    
}
