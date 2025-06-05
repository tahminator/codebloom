package com.patina.codebloom.jda;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jda.discord")
public class JDAProperties {
    private String token;
    private String id;
    private String channelId;

    public String getToken() {
        return token;
    }

    public void setToken(final String token) {
        this.token = token;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getChannelId() {
        return channelId;
    }

    public long getChannelIdAsLong() {
        return Long.valueOf(channelId);
    }

    public void setChannelId(final String channelId) {
        this.channelId = channelId;
    }
}
