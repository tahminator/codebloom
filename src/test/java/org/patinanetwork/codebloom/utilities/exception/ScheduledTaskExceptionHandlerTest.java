package org.patinanetwork.codebloom.utilities.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TaskScheduler;

public class ScheduledTaskExceptionHandlerTest {
    private final Reporter errorReporter = mock(Reporter.class);
    private final Env env = mock(Env.class);

    private final ScheduledTaskExceptionHandler scheduledTaskExceptionHandler;

    private ListAppender<ILoggingEvent> logWatcher;

    public ScheduledTaskExceptionHandlerTest() {
        scheduledTaskExceptionHandler = new ScheduledTaskExceptionHandler(errorReporter, env);
    }

    @BeforeEach
    void setup() {
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(scheduledTaskExceptionHandler.getClass())).addAppender(logWatcher);

        when(env.getActiveProfiles()).thenReturn(List.of("test"));
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(scheduledTaskExceptionHandler.getClass())).detachAndStopAllAppenders();
    }

    @Test
    void testErrorHandlerLogsException() throws InterruptedException {
        RuntimeException testException = new RuntimeException("Test exception");

        TaskScheduler scheduler = scheduledTaskExceptionHandler.taskScheduler();

        scheduler.schedule(
                () -> {
                    throw testException;
                },
                Instant.now());

        Thread.sleep(100);

        assertTrue(logWatcher.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains("Test exception")));
    }
}
