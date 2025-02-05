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
    private static final Logger log = LoggerFactory.getLogger(PotdSetter.class);

    LeetcodeApiHandler leetcodeApiHandler;
    POTDRepository potdRepository;

    public PotdSetter(LeetcodeApiHandler leetcodeApiHandler, POTDRepository potdRepository) {
        this.leetcodeApiHandler = leetcodeApiHandler;
        this.potdRepository = potdRepository;
    }

    @Scheduled(cron = "0 10 19 * * ?")
    public void SetPotd() {
        com.patina.codebloom.common.leetcode.models.POTD leetcodePotd = leetcodeApiHandler.getPotd();

        if (potdRepository.getCurrentPOTD().getTitle().equals(leetcodePotd.getTitle())) {
            // It's already the latest POTD, don't want to do it again.
            log.info("POTD has already been set before, will not be doing it again.");
            return;
        }

        potdRepository.createPOTD(new POTD(leetcodePotd.getTitle(),
                leetcodePotd.getTitleSlug(), ScoreCalculator.calculateMultiplier(leetcodePotd.getDifficulty()),
                LocalDateTime.now()));
    }
}
