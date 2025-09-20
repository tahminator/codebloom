package com.patina.codebloom.jda.properties.reporting;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.patina.codebloom.jda.properties.JDAExtendedProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "jda.discord.reporting.log")
@Getter
@AllArgsConstructor
public class JDALogReportingProperties implements JDAExtendedProperties {
    private final Long guildId;
    private final Long channelId;
}
