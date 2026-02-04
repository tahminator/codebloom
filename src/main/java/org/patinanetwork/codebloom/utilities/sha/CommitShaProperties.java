package org.patinanetwork.codebloom.utilities.sha;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.commit")
@Getter
@Setter
public class CommitShaProperties {
    private String sha = "unknown";
}
