package com.patina.codebloom.utilities;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter implements Filter {
    private static final long API_RATE_LIMIT_CAPACITY = 15L;
    private static final long API_REFILL_INTERVAL_MILLIS = 1000L;

    private static final long STATIC_RATE_LIMIT_CAPACITY = 100L;
    private static final long STATIC_REFILL_INTERVAL_MILLIS = 1000L;

    private Bucket createNewBucket(final long capacity, final long refillIntervalMillis) {
        var bandwidth = Bandwidth.builder()
                        .capacity(capacity)
                        .refillIntervally(capacity, Duration.ofMillis(refillIntervalMillis))
                        .build();

        return Bucket.builder()
                        .addLimit(bandwidth)
                        .build();
    }

    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
                    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String path = httpRequest.getServletPath();
        String remoteAddr = httpRequest.getRemoteAddr();

        boolean isApiPath = path.startsWith("/api");
        final long rateLimitCapacity = isApiPath ? API_RATE_LIMIT_CAPACITY : STATIC_RATE_LIMIT_CAPACITY;
        final long refillInterval = isApiPath ? API_REFILL_INTERVAL_MILLIS : STATIC_REFILL_INTERVAL_MILLIS;
        String bucketKey = remoteAddr + (isApiPath ? ":api" : ":static");

        HttpSession session = httpRequest.getSession();
        Bucket bucket = (Bucket) session.getAttribute(bucketKey);
        if (bucket == null) {
            bucket = createNewBucket(rateLimitCapacity, refillInterval);
            session.setAttribute(bucketKey, bucket);
        }

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Too Many Requests");
        }
    }
}