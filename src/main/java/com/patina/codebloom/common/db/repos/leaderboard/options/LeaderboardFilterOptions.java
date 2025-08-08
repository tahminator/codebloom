package com.patina.codebloom.common.db.repos.leaderboard.options;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
@ToString
@EqualsAndHashCode
public class LeaderboardFilterOptions {
    @Builder.Default
    private final int page = 1;
    @Builder.Default
    private final int pageSize = 20;
    @Builder.Default
    private final String query = "";
    @Builder.Default
    private final boolean patina = false;
    @Builder.Default
    private final boolean hunter = false;
    @Builder.Default
    private final boolean nyu = false;
    @Builder.Default
    private final boolean baruch = false;
    @Builder.Default
    private final boolean rpi = false;

    public static final LeaderboardFilterOptions DEFAULT = LeaderboardFilterOptions.builder()
                    .build();
}
