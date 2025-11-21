package com.patina.codebloom.db.leaderboard.options;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.patina.codebloom.common.db.models.usertag.Tag;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterGenerator;
import com.patina.codebloom.common.db.repos.leaderboard.options.LeaderboardFilterOptions;
import com.patina.codebloom.common.utils.pair.Pair;

public class LeaderboardFilterGeneratorTest {
    public static final Set<Tag> VALID_LEADERBOARD_TAGS = Set.of(
                    Tag.Patina,
                    Tag.Baruch,
                    Tag.Bmcc,
                    Tag.Ccny,
                    Tag.Sbu,
                    Tag.Columbia,
                    Tag.Cornell,
                    Tag.Hunter,
                    Tag.Nyu,
                    Tag.Rpi);

    public static final Set<Tag> ALL_LEADERBOARD_TAGS = Set.of(
                    Tag.Patina,
                    Tag.Gwc,
                    Tag.Baruch,
                    Tag.Bmcc,
                    Tag.Ccny,
                    Tag.Sbu,
                    Tag.Columbia,
                    Tag.Cornell,
                    Tag.Hunter,
                    Tag.Nyu,
                    Tag.Rpi);

    @Test
    void expectLeaderboardFilterGeneratorToReturnAllValidTags() {
        var leaderboardTags = new HashSet<>();
        leaderboardTags.addAll(VALID_LEADERBOARD_TAGS);
        List<Pair<LeaderboardFilterOptions, Tag>> listOfPairs = LeaderboardFilterGenerator.generateAllSupportedTagToggles();
        for (var pair : listOfPairs) {
            var tag = pair.getRight();

            if (!leaderboardTags.remove(tag)) {
                fail(String.format("Generator returned a tag that is not included in VALID_LEADERBOARD_TAGS. Tag: %s", tag.getResolvedName()));
            }
        }

        assertEquals(0, leaderboardTags.size());
    }

    @Test
    void expectBuilderWithTagToCreateCorrectFilterOptions() {
        for (Tag tag : ALL_LEADERBOARD_TAGS) {
            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(tag).build();

            switch (tag) {
                case Patina -> assertTrue(options.isPatina());
                case Gwc -> assertTrue(options.isGwc());
                case Baruch -> assertTrue(options.isBaruch());
                case Bmcc -> assertTrue(options.isBmcc());
                case Ccny -> assertTrue(options.isCcny());
                case Sbu -> assertTrue(options.isSbu());
                case Columbia -> assertTrue(options.isColumbia());
                case Cornell -> assertTrue(options.isCornell());
                case Hunter -> assertTrue(options.isHunter());
                case Nyu -> assertTrue(options.isNyu());
                case Rpi -> assertTrue(options.isRpi());
            }
        }
    }

    @Test
    void expectBuilderWithTagToSetOnlyOneTagTrue() {
        for (Tag tag : ALL_LEADERBOARD_TAGS) {
            LeaderboardFilterOptions options = LeaderboardFilterGenerator.builderWithTag(tag).build();

            int trueCount = 0;
            if (options.isPatina()) {
                trueCount++;
            }
            if (options.isGwc()) {
                trueCount++;
            }
            if (options.isBaruch()) {
                trueCount++;
            }
            if (options.isBmcc()) {
                trueCount++;
            }
            if (options.isCcny()) {
                trueCount++;
            }
            if (options.isSbu()) {
                trueCount++;
            }
            if (options.isColumbia()) {
                trueCount++;
            }
            if (options.isCornell()) {
                trueCount++;
            }
            if (options.isHunter()) {
                trueCount++;
            }
            if (options.isNyu()) {
                trueCount++;
            }
            if (options.isRpi()) {
                trueCount++;
            }

            assertEquals(1, trueCount, "Expected exactly one tag to be true for tag: " + tag.getResolvedName());
        }
    }
}
