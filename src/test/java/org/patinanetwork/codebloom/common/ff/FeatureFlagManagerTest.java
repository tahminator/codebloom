package org.patinanetwork.codebloom.common.ff;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.jda.properties.FeatureFlagConfiguration;

class FeatureFlagManagerTest {

    private FeatureFlagManager featureFlagManager;

    @BeforeEach
    void setUp() {
        FeatureFlagConfiguration ff = new FeatureFlagConfiguration();
        ff.setDuels(true);
        ff.setUserMetrics(false);
        featureFlagManager = new FeatureFlagManager(ff);
    }

    @Test
    void validateExpressionFlagsExistAllowsKnownFlags() {
        assertDoesNotThrow(() -> featureFlagManager.validateExpressionFlagsExist("duels && userMetrics"));
    }

    @Test
    void validateExpressionFlagsExistAllowsReservedIdentifiers() {
        assertDoesNotThrow(() -> featureFlagManager.validateExpressionFlagsExist("duels && true && !false"));
    }

    @Test
    void validateExpressionFlagsExistThrowsWhenFlagUnknown() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> featureFlagManager.validateExpressionFlagsExist("duels && school"));

        assertTrue(exception.getMessage().contains("school"));
    }

    @Test
    void getAllFlagsReturnsValuesFromConfiguration() {
        var flags = featureFlagManager.getAllFlags();

        assertEquals(2, flags.size());
        assertEquals(Boolean.TRUE, flags.get("duels"));
        assertEquals(Boolean.FALSE, flags.get("userMetrics"));
    }

    @Test
    void getAllFlagsReturnsImmutableView() {
        var flags = featureFlagManager.getAllFlags();

        assertThrows(UnsupportedOperationException.class, () -> flags.put("newFlag", true));
        assertFalse(flags.containsKey("newFlag"));
    }
}
