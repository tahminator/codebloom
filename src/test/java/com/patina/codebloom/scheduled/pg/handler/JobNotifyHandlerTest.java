package com.patina.codebloom.scheduled.pg.handler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.patina.codebloom.scheduled.leetcode.LeetcodeQuestionProcessService;

public class JobNotifyHandlerTest {

    private LeetcodeQuestionProcessService leetcodeQuestionProcessService;
    private JobNotifyHandler jobNotifyHandler;

    @BeforeEach
    void setUp() {
        leetcodeQuestionProcessService = mock(LeetcodeQuestionProcessService.class);
        jobNotifyHandler = new JobNotifyHandler(leetcodeQuestionProcessService);
    }

    @Test
    void testHandleCallsDrainQueue() {
        String testPayload = "test-payload";

        jobNotifyHandler.handle(testPayload);

        verify(leetcodeQuestionProcessService, times(1)).drainQueue();
    }

    @Test
    void testHandleWithNullPayload() {
        jobNotifyHandler.handle(null);

        verify(leetcodeQuestionProcessService, times(1)).drainQueue();
    }

    @Test
    void testHandleWithEmptyPayload() {
        jobNotifyHandler.handle("");

        verify(leetcodeQuestionProcessService, times(1)).drainQueue();
    }

    @Test
    void testHandleWithValidPayload() {
        String jsonPayload = "{\"id\": 123, \"type\": \"process\"}";

        jobNotifyHandler.handle(jsonPayload);

        verify(leetcodeQuestionProcessService, times(1)).drainQueue();
    }

    @Test
    void testMultipleHandleCalls() {
        jobNotifyHandler.handle("payload1");
        jobNotifyHandler.handle("payload2");
        jobNotifyHandler.handle("payload3");

        verify(leetcodeQuestionProcessService, times(3)).drainQueue();
    }
}
