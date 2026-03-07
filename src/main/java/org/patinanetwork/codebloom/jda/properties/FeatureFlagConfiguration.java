package org.patinanetwork.codebloom.jda.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ff")
public class FeatureFlagConfiguration {
    private boolean duels;
}
