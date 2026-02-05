package org.patinanetwork.codebloom.utilities.exception;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.slf4j.LoggerFactory;

public class ControllerExceptionHandlerTest {
    private final Reporter errorReporter = mock(Reporter.class);
    private final Env env = mock(Env.class);

    private final ControllerExceptionHandler controllerExceptionHandler;

    private ListAppender<ILoggingEvent> logWatcher;

    public ControllerExceptionHandlerTest() {
        controllerExceptionHandler = new ControllerExceptionHandler(errorReporter, env);
    }

    @BeforeEach
    void setup() {
        logWatcher = new ListAppender<>();
        logWatcher.start();
        ((Logger) LoggerFactory.getLogger(controllerExceptionHandler.getClass())).addAppender(logWatcher);
    }

    @AfterEach
    void teardown() {
        ((Logger) LoggerFactory.getLogger(controllerExceptionHandler.getClass())).detachAndStopAllAppenders();
    }

    @Test
    void testHandleExceptionLogsError() {
        controllerExceptionHandler.handleThrowable(new Exception("Expected error!"));

        assertTrue(logWatcher.list.stream()
                .anyMatch(log -> log.getLevel().equals(Level.ERROR)
                        && log.getFormattedMessage().contains("Expected error!")));
    }
}
