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
    private static final long RATE_LIMIT_CAPACITY = 1L;
    private static final long REFILL_INTERVAL_MILLIS = 100L;

    private Bucket createNewBucket() {
        var bandwidth = Bandwidth.builder()
                        .capacity(RATE_LIMIT_CAPACITY)
                        .refillIntervally(RATE_LIMIT_CAPACITY, Duration.ofMillis(REFILL_INTERVAL_MILLIS))
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
        HttpServletRequest httpRequest = request;
        HttpServletResponse httpResponse = response;

        String path = httpRequest.getServletPath();

        if (!path.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(false);
        String remoteAddr = request.getRemoteAddr();
        Bucket bucket = null;

        if (session != null) {
            bucket = (Bucket) session.getAttribute(remoteAddr);
        }

        if (bucket == null) {
            bucket = createNewBucket();
            if (session == null) {
                session = httpRequest.getSession(true);
            }
            session.setAttribute(remoteAddr, bucket);
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