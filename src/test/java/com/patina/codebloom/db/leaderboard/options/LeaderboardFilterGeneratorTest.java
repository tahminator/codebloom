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
}
