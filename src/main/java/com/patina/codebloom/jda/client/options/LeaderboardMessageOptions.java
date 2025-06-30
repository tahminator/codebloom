package com.patina.codebloom.jda.client.options;

import java.awt.Color;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaderboardMessageOptions {
    private final String title;
    private final String description;
    private final String footerText;
    private final String footerIcon;
    private final Color color;
    private final String guildId;
    private final long channelId;
    private final byte[] screenshotBytes;
}
