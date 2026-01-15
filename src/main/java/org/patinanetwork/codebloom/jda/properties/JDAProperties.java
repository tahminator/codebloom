package org.patinanetwork.codebloom.jda.properties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jda.discord")
@AllArgsConstructor
@Getter
public class JDAProperties {

    private String token;
}
