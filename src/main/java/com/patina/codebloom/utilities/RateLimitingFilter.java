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
    private static final long REQUESTS_OVER_TIME = 1L;
    private static final long MILLISECONDS_TO_WAIT = 100L;

    private Bucket createNewBucket() {
        var bandwidth = Bandwidth.builder()
                        .capacity(REQUESTS_OVER_TIME)
                        .refillIntervally(REQUESTS_OVER_TIME, Duration.ofMillis(MILLISECONDS_TO_WAIT))
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

        if (!path.startsWith("/api")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = httpRequest.getSession(true);

        String remoteAddr = request.getRemoteAddr();
        Bucket bucket = (Bucket) session.getAttribute(remoteAddr);

        if (bucket == null) {
            bucket = createNewBucket();
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