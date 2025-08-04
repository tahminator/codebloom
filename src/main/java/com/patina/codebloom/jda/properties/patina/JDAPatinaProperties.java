package com.patina.codebloom.jda.properties.patina;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.patina.codebloom.jda.properties.JDAExtendedProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;

@ConfigurationProperties(prefix = "jda.discord.patina")
@AllArgsConstructor
@Getter
public class JDAPatinaProperties implements JDAExtendedProperties {
    private final Long guildId;
    private final Long leetcodeChannelId;
}
