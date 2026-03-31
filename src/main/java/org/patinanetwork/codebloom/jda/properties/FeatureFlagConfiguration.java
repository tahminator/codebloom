package org.patinanetwork.codebloom.jda.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/** Feature flags that we supported. Must be defined through application.yml */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ff")
public class FeatureFlagConfiguration {
    private boolean duels;

    private boolean userMetrics;
}
