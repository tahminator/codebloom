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

    @Builder.Default
    private List<byte[]> filesBytes = List.of();

    @Builder.Default
    private List<String> fileNames = List.of();
}
