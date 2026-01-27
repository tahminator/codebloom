package org.patinanetwork.codebloom.common.db.repos.leaderboard.options;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
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

    @Builder.Default
    private final boolean gwc = false;

    @Builder.Default
    private final boolean sbu = false;

    @Builder.Default
    private final boolean ccny = false;

    @Builder.Default
    private final boolean columbia = false;

    @Builder.Default
    private final boolean cornell = false;

    @Builder.Default
    private final boolean bmcc = false;

    @Builder.Default
    private final boolean mhcplusplus = false;

    public static final LeaderboardFilterOptions DEFAULT =
            LeaderboardFilterOptions.builder().build();
}
