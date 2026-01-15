package org.patinanetwork.codebloom.common.db.repos.leaderboard.options;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;
import org.patinanetwork.codebloom.common.utils.pair.Pair;

public class LeaderboardFilterGenerator {

    /** Return a list of {@link LeaderboardFilterOptions} where each opts object will have exactly one flag on. */
    public static List<Pair<LeaderboardFilterOptions, Tag>> generateAllSupportedTagToggles() {
        List<Pair<LeaderboardFilterOptions, Tag>> list = new ArrayList<>();

        list.add(Pair.of(withOnlyTrue(opt -> opt.sbu(true)), Tag.Sbu));
        list.add(Pair.of(withOnlyTrue(opt -> opt.patina(true)), Tag.Patina));
        list.add(Pair.of(withOnlyTrue(opt -> opt.hunter(true)), Tag.Hunter));
        list.add(Pair.of(withOnlyTrue(opt -> opt.nyu(true)), Tag.Nyu));
        list.add(Pair.of(withOnlyTrue(opt -> opt.baruch(true)), Tag.Baruch));
        list.add(Pair.of(withOnlyTrue(opt -> opt.rpi(true)), Tag.Rpi));
        list.add(Pair.of(withOnlyTrue(opt -> opt.ccny(true)), Tag.Ccny));
        list.add(Pair.of(withOnlyTrue(opt -> opt.columbia(true)), Tag.Columbia));
        list.add(Pair.of(withOnlyTrue(opt -> opt.cornell(true)), Tag.Cornell));
        list.add(Pair.of(withOnlyTrue(opt -> opt.bmcc(true)), Tag.Bmcc));

        return list;
    }

    public static LeaderboardFilterOptions.LeaderboardFilterOptionsBuilder builderWithTag(final Tag tag) {
        return switch (tag) {
            case Patina -> LeaderboardFilterOptions.builder().patina(true);
            case Hunter -> LeaderboardFilterOptions.builder().hunter(true);
            case Nyu -> LeaderboardFilterOptions.builder().nyu(true);
            case Baruch -> LeaderboardFilterOptions.builder().baruch(true);
            case Rpi -> LeaderboardFilterOptions.builder().rpi(true);
            case Gwc -> LeaderboardFilterOptions.builder().gwc(true);
            case Sbu -> LeaderboardFilterOptions.builder().sbu(true);
            case Ccny -> LeaderboardFilterOptions.builder().ccny(true);
            case Columbia -> LeaderboardFilterOptions.builder().columbia(true);
            case Cornell -> LeaderboardFilterOptions.builder().cornell(true);
            case Bmcc -> LeaderboardFilterOptions.builder().bmcc(true);
        };
    }

    private static LeaderboardFilterOptions withOnlyTrue(
            final Consumer<LeaderboardFilterOptions.LeaderboardFilterOptionsBuilder> flagSetter) {
        LeaderboardFilterOptions.LeaderboardFilterOptionsBuilder builder = LeaderboardFilterOptions.builder();

        flagSetter.accept(builder);
        return builder.build();
    }
}
