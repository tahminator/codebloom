package org.patinanetwork.codebloom.common.reporter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.jda.client.JDAClient;

class ReporterTest {

    private TestableReporter reporter;
    private JDAClient jdaClient;
    private Instant startTime;

    // use clock to fake time change
    private static class TestableReporter extends Reporter {
        private Clock clock;

        TestableReporter(JDAClient jdaClient, Clock clock) {
            super(jdaClient);
            this.clock = clock;
        }

        public void setClock(Clock clock) {
            this.clock = clock;
        }

        @Override
        protected long now() {
            return clock.millis();
        }
    }

    @BeforeEach
    void setUp() {
        jdaClient = mock(JDAClient.class);
        startTime = Instant.parse("2025-12-27T07:00:00Z");
        Clock fixedClock = Clock.fixed(startTime, ZoneId.of("UTC"));
        reporter = new TestableReporter(jdaClient, fixedClock);
    }

    @Test
    @DisplayName("Should reset counter after the 30-minute window expires")
    void testExpirationLogic() {
        String key = "expiry-test";

        reporter.shouldReport(key);

        Instant later = startTime.plus(Duration.ofMinutes(31));
        reporter.setClock(Clock.fixed(later, ZoneId.of("UTC")));

        assertFalse(reporter.shouldReport(key), "Should start a new window after expiry");

        assertFalse(reporter.shouldReport(key));
        assertTrue(reporter.shouldReport(key));
    }

    @Test
    @DisplayName("Should only return true on the 3rd occurrence (Threshold Check)")
    void testThresholdReporting() {
        String key = "test-error";

        assertFalse(reporter.shouldReport(key), "1st occurrence should not report");
        assertFalse(reporter.shouldReport(key), "2nd occurrence should not report");
        assertTrue(reporter.shouldReport(key), "3rd occurrence should report");

        assertFalse(reporter.shouldReport(key), "Post-report occurrence should reset and not report");
    }

    @Test
    @DisplayName("Should handle high concurrency correctly without over-reporting")
    void testConcurrency() throws InterruptedException {
        String key = "concurrent-key";
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.execute(() -> {
                if (reporter.shouldReport(key)) {
                    successCount.incrementAndGet();
                }
                latch.countDown();
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 10 attempts / Threshold of 3 = 3 successful reports (with removal on success)
        assertEquals(3, successCount.get(), "Should have triggered exactly 3 reports for 10 concurrent calls");
    }

    @Test
    @DisplayName("Cleanup task should remove expired entries")
    void testCleanUp() {
        String key = "expired-key";

        reporter.shouldReport(key);

        Instant later = startTime.plus(Duration.ofMinutes(31));
        reporter.setClock(Clock.fixed(later, ZoneId.of("UTC")));

        reporter.cleanUp();

        assertFalse(reporter.shouldReport(key), "Key should have been removed by cleanup");
        assertFalse(reporter.shouldReport(key));
        assertTrue(reporter.shouldReport(key));
    }
}
