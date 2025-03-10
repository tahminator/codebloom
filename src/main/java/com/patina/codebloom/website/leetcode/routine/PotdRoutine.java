package com.patina.codebloom.website.leetcode.routine;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.patina.codebloom.website.leetcode.client.LeetcodeApiClient;
import com.patina.codebloom.website.leetcode.client.model.LeetcodePOTD;
import com.patina.codebloom.website.leetcode.client.score.ScoreCalculator;
import com.patina.codebloom.website.leetcode.model.POTD;
import com.patina.codebloom.website.leetcode.repo.POTDRepository;

/**
 * This automatic routine is used to capture the POTD from
 * Leetcode directly. This routine runs frequently so that
 * it is unlikely that the POTD can be unsynced for very
 * long.
 */
@Component
public class PotdRoutine {
    private static final Logger LOGGER = LoggerFactory.getLogger(PotdRoutine.class);

    private LeetcodeApiClient leetcodeApiClient;
    private POTDRepository potdRepository;

    public PotdRoutine(final LeetcodeApiClient leetcodeApiClient, final POTDRepository potdRepository) {
        this.leetcodeApiClient = leetcodeApiClient;
        this.potdRepository = potdRepository;
    }

    @Scheduled(initialDelay = 0, fixedDelay = 1000 * 60 * 5)
    public void setPotd() {
        LeetcodePOTD leetcodePotd = leetcodeApiClient.getPotd();

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
