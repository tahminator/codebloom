package org.patinanetwork.codebloom.jda.properties.reporting;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.patinanetwork.codebloom.jda.properties.JDAExtendedProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jda.discord.reporting.error")
@Getter
@AllArgsConstructor
public class JDAErrorReportingProperties implements JDAExtendedProperties {

    private final Long guildId;
    private final Long channelId;
}
