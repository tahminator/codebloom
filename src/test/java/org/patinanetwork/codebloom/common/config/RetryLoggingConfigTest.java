package org.patinanetwork.codebloom.common.config;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.resilience4j.core.EventConsumer;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.retry.event.RetryOnRetryEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;

@ExtendWith(OutputCaptureExtension.class)
public class RetryLoggingConfigTest {

    private RetryRegistry retryRegistry;
    private Retry retry;
    private Retry.EventPublisher retryPublisher;
    private RetryRegistry.EventPublisher<Retry> registryPublisher;

    private RetryLoggingConfig retryLoggingConfig;

    @BeforeEach
    void setup() {
        retryRegistry = mock(RetryRegistry.class);
        retry = mock(Retry.class);
        retryPublisher = mock(Retry.EventPublisher.class);
        registryPublisher = mock(RetryRegistry.EventPublisher.class);

        when(retry.getEventPublisher()).thenReturn(retryPublisher);
        when(retryRegistry.getEventPublisher()).thenReturn(registryPublisher);
        when(retryRegistry.getAllRetries()).thenReturn(java.util.Set.of(retry));

        retryLoggingConfig = new RetryLoggingConfig(retryRegistry);
    }

    @Test
    void registerRetryLoggingExistingRetry(CapturedOutput output) {
        RetryOnRetryEvent event = mock(RetryOnRetryEvent.class);

        when(event.getName()).thenReturn("existingRetry");
        when(event.getEventType()).thenReturn(io.github.resilience4j.retry.event.RetryEvent.Type.RETRY);
        when(event.getNumberOfRetryAttempts()).thenReturn(1);
        when(event.getLastThrowable()).thenReturn(new RuntimeException("Unique Error Message"));

        ArgumentCaptor<EventConsumer<RetryOnRetryEvent>> captor = ArgumentCaptor.forClass(EventConsumer.class);

        retryLoggingConfig.registerRetryLogging();

        verify(retryRegistry).getAllRetries();
        verify(retry).getEventPublisher();
        verify(retryPublisher).onRetry(captor.capture());

        EventConsumer<RetryOnRetryEvent> listener = captor.getValue();

        assertNotNull(listener);
        assertDoesNotThrow(() -> listener.consumeEvent(event));
        assertTrue(output.getOut().contains("Unique Error Message"));

        verify(event).getName();
        verify(event).getEventType();
        verify(event).getNumberOfRetryAttempts();
        verify(event, atLeast(1)).getLastThrowable();
    }
}
