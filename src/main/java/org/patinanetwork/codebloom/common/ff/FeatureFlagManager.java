package org.patinanetwork.codebloom.common.ff;

import java.beans.Introspector;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.patinanetwork.codebloom.jda.properties.FeatureFlagConfiguration;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagManager {
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
}
