package org.patinanetwork.codebloom.scheduled.potd;

import org.patinanetwork.codebloom.common.db.models.potd.POTD;
import org.patinanetwork.codebloom.common.db.repos.potd.POTDRepository;
import org.patinanetwork.codebloom.common.leetcode.LeetcodeClient;
import org.patinanetwork.codebloom.common.leetcode.score.ScoreCalculator;
import org.patinanetwork.codebloom.common.leetcode.throttled.ThrottledLeetcodeClient;
import org.patinanetwork.codebloom.common.time.StandardizedLocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Profile("!ci")
public class PotdSetter {

    private static final Logger LOGGER = LoggerFactory.getLogger(PotdSetter.class);

    private LeetcodeClient leetcodeClient;
    private POTDRepository potdRepository;

    public PotdSetter(final ThrottledLeetcodeClient throttledLeetcodeClient, final POTDRepository potdRepository) {
        this.leetcodeClient = throttledLeetcodeClient;
        this.potdRepository = potdRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 5)
    public void setPotd() {
        org.patinanetwork.codebloom.common.leetcode.models.POTD leetcodePotd = leetcodeClient.getPotd();

        if (leetcodePotd == null) {
            LOGGER.warn("No POTD was been returned.");
            return;
        }

        POTD currentPotd = potdRepository.getCurrentPOTD();
        if (currentPotd != null && currentPotd.getTitle().equals(leetcodePotd.getTitle())) {
            // It's already the latest POTD, don't want to do it again.
            LOGGER.info("POTD has already been set before, will not be doing it again.");
            return;
        }

        potdRepository.createPOTD(POTD.builder()
                .title(leetcodePotd.getTitle())
                .slug(leetcodePotd.getTitleSlug())
                .multiplier(ScoreCalculator.calculateMultiplier((leetcodePotd.getDifficulty())))
                .createdAt(StandardizedLocalDateTime.now())
                .build());
    }
}
