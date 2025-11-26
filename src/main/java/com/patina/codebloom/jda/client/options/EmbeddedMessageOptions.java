package com.patina.codebloom.jda.client.options;

import java.awt.Color;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmbeddedMessageOptions {

    private String title;
    private String description;
    private String footerText;
    private String footerIcon;
    private Color color;
    private long guildId;
    private long channelId;
    private byte[] fileBytes;
    private String fileName;
}
