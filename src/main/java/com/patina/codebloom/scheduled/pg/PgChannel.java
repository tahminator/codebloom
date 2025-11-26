package com.patina.codebloom.scheduled.pg;

import java.util.Arrays;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

/**
 * PG NOTIFY channels.
 */
@Getter
@ToString
public enum PgChannel {
    INSERT_JOB("jobInsertChannel"),
    UPSERT_LOBBY("upsertLobbyChannel");

    private final String channelName;

    PgChannel(final String channelName) {
        this.channelName = channelName;
    }

    public static List<PgChannel> list() {
        return Arrays.asList(values());
    }

    public static PgChannel fromChannelName(final String channelName) {
        return list()
            .stream()
            .filter(c -> c.getChannelName().equals(channelName))
            .findFirst()
            .orElseThrow();
    }
}
