package com.patina.codebloom.scheduled.pg.handler;

import com.patina.codebloom.scheduled.leetcode.LeetcodeQuestionProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("!ci | thread")
public class JobNotifyHandler {

    private final LeetcodeQuestionProcessService leetcodeQuestionProcessService;

    public JobNotifyHandler(
        final LeetcodeQuestionProcessService leetcodeQuestionProcessService
    ) {
        this.leetcodeQuestionProcessService = leetcodeQuestionProcessService;
    }

    @Async
    public void handle(final String payload) {
        leetcodeQuestionProcessService.drainQueue();
    }
}
