package org.patinanetwork.codebloom.common.ff;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.ff.annotation.FF;
import org.patinanetwork.codebloom.jda.properties.FeatureFlagConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest(classes = FFAspectTest.TestConfig.class)
class FFAspectTest {

    @Autowired
    private TestService testService;

    @BeforeEach
    void resetCallCount() {
        testService.resetCalls();
    }

    @Test
    @DisplayName("Allows method execution when expression is true")
    void allowsWhenTrue() {
        String result = testService.methodWithEnabledFlag();

        assertEquals("ok", result);
        assertEquals(1, testService.getCalls());
    }

    @Test
    @DisplayName("Throws forbidden when expression is false")
    void forbiddenWhenFalse() {
        ResponseStatusException exception =
                assertThrows(ResponseStatusException.class, () -> testService.methodWithDisabledFlag());

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
        assertEquals(0, testService.getCalls());
    }

    @Test
    @DisplayName("Throws when unknown flag is used")
    void throwsOnUnknownFlag() {
        assertThrows(IllegalArgumentException.class, () -> testService.methodWithUnknownFlag());
        assertEquals(0, testService.getCalls());
    }

    @Test
    @DisplayName("Throws when expression is invalid")
    void throwsOnInvalidExpression() {
        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> testService.methodWithInvalidExpression());

        assertTrue(exception.getMessage().contains("Invalid @FF expression"));
        assertEquals(0, testService.getCalls());
    }

    @Test
    @DisplayName("Allows literal expressions")
    void allowsLiterals() {
        String result = testService.methodWithLiteralTrueExpression();

        assertEquals("ok", result);
        assertEquals(1, testService.getCalls());
    }

    public static class TestService {
        private int calls;

        @FF("duels")
        public String methodWithEnabledFlag() {
            calls++;
            return "ok";
        }

        @FF("duels && userMetrics")
        public String methodWithDisabledFlag() {
            calls++;
            return "no";
        }

        @FF("duels && school")
        public String methodWithUnknownFlag() {
            calls++;
            return "no";
        }

        @FF("duels &&")
        public String methodWithInvalidExpression() {
            calls++;
            return "no";
        }

        @FF("true")
        public String methodWithLiteralTrueExpression() {
            calls++;
            return "ok";
        }

        public int getCalls() {
            return calls;
        }

        public void resetCalls() {
            calls = 0;
        }
    }

    @TestConfiguration
    @EnableAspectJAutoProxy
    static class TestConfig {

        @Bean
        public FeatureFlagConfiguration featureFlagConfiguration() {
            FeatureFlagConfiguration ff = new FeatureFlagConfiguration();
            ff.setDuels(true);
            ff.setUserMetrics(false);
            return ff;
        }

        @Bean
        public FeatureFlagManager featureFlagManager(final FeatureFlagConfiguration ff) {
            return new FeatureFlagManager(ff);
        }

        @Bean
        public FFAspect ffAspect(final FeatureFlagManager featureFlagManager) {
            return new FFAspect(featureFlagManager);
        }

        @Bean
        public TestService testService() {
            return new TestService();
        }
    }
}
