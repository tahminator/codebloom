package com.patina.codebloom.common.db.repos.leaderboard.options;

import com.patina.codebloom.common.db.models.usertag.Tag;

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
    public static final LeaderboardFilterOptions DEFAULT = LeaderboardFilterOptions.builder()
                    .build();

    public static LeaderboardFilterOptionsBuilder builderWithTag(final Tag tag) {
        return switch (tag) {
            case Patina -> builder().patina(true);
            case Hunter -> builder().hunter(true);
            case Nyu -> builder().nyu(true);
            case Baruch -> builder().baruch(true);
            case Rpi -> builder().rpi(true);
            case Gwc -> builder().gwc(true);
            case Sbu -> builder().sbu(true);
            case Ccny -> builder().ccny(true);
            case Columbia -> builder().columbia(true);
            case Cornell -> builder().cornell(true);
            case Bmcc -> builder().bmcc(true);
        };
    }
}
