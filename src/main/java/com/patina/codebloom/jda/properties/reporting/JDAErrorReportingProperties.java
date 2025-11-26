package com.patina.codebloom.jda.properties.reporting;

import com.patina.codebloom.jda.properties.JDAExtendedProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jda.discord.reporting.error")
@Getter
@AllArgsConstructor
public class JDAErrorReportingProperties implements JDAExtendedProperties {

    private final Long guildId;
    private final Long channelId;
}
