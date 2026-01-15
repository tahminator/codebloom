package org.patinanetwork.codebloom.jda.client.options;

import java.awt.Color;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmbeddedImagesMessageOptions {

    private String title;
    private String description;
    private String footerText;
    private String footerIcon;
    private Color color;
    private long guildId;
    private long channelId;
    private List<byte[]> filesBytes;
    private List<String> fileNames;
}
