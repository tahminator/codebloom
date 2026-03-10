package org.patinanetwork.codebloom.common.config;

import io.github.resilience4j.core.registry.EntryAddedEvent;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RetryLoggingConfig {

    private final RetryRegistry retryRegistry;

    @PostConstruct
    public void registerRetryLogging() {
        retryRegistry.getAllRetries().forEach(this::attachRetryLogger);

        retryRegistry.getEventPublisher().onEntryAdded(this::handleRetryAdded);
    }

    private void handleRetryAdded(EntryAddedEvent<Retry> event) {
        attachRetryLogger(event.getAddedEntry());
    }

    private void attachRetryLogger(Retry retry) {
        retry.getEventPublisher()
                .onRetry(event -> log.info(
                        "Retry event for '{}': type={}, totalAttempts={}, lastError='{}'",
                        event.getName(),
                        event.getEventType(),
                        event.getNumberOfRetryAttempts(),
                        event.getLastThrowable() != null
                                ? event.getLastThrowable().getMessage()
                                : "N/A"));
    }
}
