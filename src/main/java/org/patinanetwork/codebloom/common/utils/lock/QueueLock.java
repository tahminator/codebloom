package org.patinanetwork.codebloom.common.utils.lock;

import com.google.common.annotations.VisibleForTesting;
import io.github.bucket4j.BlockingBucket;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import lombok.extern.slf4j.Slf4j;

/**
 * {@code QueueLock} is an abstraction over {@code BlockingBucket} that provides rate-limiting / bucket operations where
 * the calling thread will wait until it's position in the queue has been reached, at which point execution will
 * continue.
 *
 * <p>{@code acquire()} will append the thread to the end of the queue.
 *
 * <p>{@code acquireFast()} will append the thread to the start of the queue.
 */
@Slf4j
public class QueueLock {
    @VisibleForTesting
    final BlockingDeque<CountDownLatch> queue;

    private final BlockingBucket bucket;
    private final ExecutorService pool;

    public QueueLock(BlockingBucket bucket, ExecutorService virtualPool) {
        this.bucket = bucket;

        this.queue = new LinkedBlockingDeque<>();
        this.pool = virtualPool;

        this.pool.submit(this::ticker);
    }

    private void ticker() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                // wait until something is available.
                var latch = queue.take();

                // wait until bucket is ready to accept next request
                bucket.consume(1);

                // release thread
                latch.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void queue(boolean fastTrack) throws InterruptedException {
        var latch = new CountDownLatch(1);

        if (fastTrack) {
            queue.offerFirst(latch);
        } else {
            queue.offerLast(latch);
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error("QueueLock interrupted", e);
            throw e;
        }
    }

    /** This method will append the thread to the end of the queue. */
    public void acquire() throws InterruptedException {
        queue(false);
    }

    /** This method will append the thread to the start of the queue. */
    public void acquireFast() throws InterruptedException {
        queue(true);
    }
}
