package org.patinanetwork.codebloom.common.security.annotation.featureflag;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class FeatureFlagManager {
    private final Map<String, Boolean> flags = Map.of(
            "duel", false,
            "school", false);

    public boolean exists(String flag) {
        return flags.containsKey(flag);
    }

    public boolean isEnabled(String flag) {
        return flags.getOrDefault(flag, false);
    }
}
