package org.patinanetwork.codebloom.common.ff;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.patinanetwork.codebloom.jda.properties.FeatureFlagConfiguration;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagManager {
    private static final Pattern IDENTIFIER_PATTERN = Pattern.compile("\\b[a-zA-Z_][a-zA-Z0-9_]*\\b");

    private static final Set<String> RESERVED_IDENTIFIERS = Set.of(
            "true",
            "false",
            "null",
            "and",
            "or",
            "not",
            "eq",
            "ne",
            "lt",
            "le",
            "gt",
            "ge",
            "between",
            "instanceof",
            "matches");

    private final FeatureFlagConfiguration ff;
    private final Map<String, Boolean> cachedFlags;

    public FeatureFlagManager(final FeatureFlagConfiguration ff) {
        this.ff = ff;
        this.cachedFlags = initializeFlags();
    }

    private Map<String, Boolean> initializeFlags() {
        Map<String, Boolean> flags = new HashMap<>();
        for (Method method : ff.getClass().getMethods()) {
            if (method.getName().startsWith("is")
                    && method.getReturnType() == boolean.class
                    && method.getParameterCount() == 0) {
                try {
                    String flagName = Introspector.decapitalize(method.getName().substring(2));
                    flags.put(flagName, (Boolean) method.invoke(ff));
                } catch (Exception e) {
                    throw new IllegalStateException(
                            "Failed to resolve feature flag value from method: " + method.getName(), e);
                }
            }
        }
        return Map.copyOf(flags);
    }

    public Map<String, Boolean> getAllFlags() {
        return cachedFlags;
    }

    public void validateExpressionFlagsExist(final String expression) {
        Map<String, Boolean> flags = getAllFlags();
        Matcher matcher = IDENTIFIER_PATTERN.matcher(expression);

        while (matcher.find()) {
            String token = matcher.group();

            if (RESERVED_IDENTIFIERS.contains(token)) {
                continue;
            }

            if (!flags.containsKey(token)) {
                throw new IllegalArgumentException("Unknown feature flag in @FF expression: " + token);
            }
        }
    }
}
