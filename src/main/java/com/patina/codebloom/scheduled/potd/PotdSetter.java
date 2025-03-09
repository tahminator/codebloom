package com.patina.codebloom.scheduled.potd;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.common.db.models.potd.POTD;
import com.patina.codebloom.common.db.repos.potd.POTDRepository;
import com.patina.codebloom.common.leetcode.LeetcodeApiHandler;
import com.patina.codebloom.common.leetcode.score.ScoreCalculator;

@Component
public class PotdSetter {
    private static final Logger LOGGER = LoggerFactory.getLogger(PotdSetter.class);

    private LeetcodeApiHandler leetcodeApiHandler;
    private POTDRepository potdRepository;

    public PotdSetter(final LeetcodeApiHandler leetcodeApiHandler, final POTDRepository potdRepository) {
        this.leetcodeApiHandler = leetcodeApiHandler;
        this.potdRepository = potdRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 5)
    public void setPotd() {
        com.patina.codebloom.common.leetcode.models.POTD leetcodePotd = leetcodeApiHandler.getPotd();

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

        potdRepository.createPOTD(new POTD(leetcodePotd.getTitle(), leetcodePotd.getTitleSlug(),
                        ScoreCalculator.calculateMultiplier(leetcodePotd.getDifficulty()), LocalDateTime.now()));
    }
}
