package com.patina.codebloom.utilities;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

@Component
public class RateLimitingFilter implements Filter {
    private static final long API_RATE_LIMIT_CAPACITY = 1L;
    private static final long API_REFILL_INTERVAL_MILLIS = 100L;

    private static final long STATIC_RATE_LIMIT_CAPACITY = 10L;
    private static final long STATIC_REFILL_INTERVAL_MILLIS = 1000L;

    private Bucket createNewBucket(long capacity, long refillIntervalMillis) {
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
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
                    throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getServletPath();
        String remoteAddr = request.getRemoteAddr();

        long rateLimitCapacity;
        long refillInterval;
        String bucketKey;

        if (path.startsWith("/api")) {
            rateLimitCapacity = API_RATE_LIMIT_CAPACITY;
            refillInterval = API_REFILL_INTERVAL_MILLIS;
            bucketKey = remoteAddr + ":api";
        } else {
            rateLimitCapacity = STATIC_RATE_LIMIT_CAPACITY;
            refillInterval = STATIC_REFILL_INTERVAL_MILLIS;
            bucketKey = remoteAddr + ":static";
        }

        HttpSession session = httpRequest.getSession(false);
        Bucket bucket = null;

        if (session != null) {
            bucket = (Bucket) session.getAttribute(bucketKey);
        }

        if (bucket == null) {
            bucket = createNewBucket(rateLimitCapacity, refillInterval);
            if (session == null) {
                session = httpRequest.getSession(true);
            }
            session.setAttribute(bucketKey, bucket);
        }

        if (bucket.tryConsume(1)) {
            chain.doFilter(request, response);
        } else {
            httpResponse.setContentType("text/plain");
            httpResponse.setStatus(429);
            httpResponse.getWriter().append("Number of requests exceeds the limit");
        }
    }
}