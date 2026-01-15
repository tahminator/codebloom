package org.patinanetwork.codebloom.jda.properties.patina;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patinanetwork.codebloom.jda.properties.JDAExtendedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jda.discord.patina")
@AllArgsConstructor
@Getter
public class JDAPatinaProperties implements JDAExtendedProperties {

    private final Long guildId;
    private final Long leetcodeChannelId;
}
