package org.patinanetwork.codebloom.common.ff.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Feature flag gate
 *
 * <p>The value must be a SpEL boolean expression using feature flag names from FeatureFlagManager. Example:
 * {@code "duels && userMetrics"}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FF {
    String value();
}
