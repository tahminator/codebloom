package com.patina.codebloom.utilities;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

class RateLimitingFilterTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpSession session;

    private RateLimitingFilter filter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new RateLimitingFilter();

        when(request.getServletPath()).thenReturn("/api/user/all");
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(request.getSession()).thenReturn(session);
    }

    @Test
    void testApiRateLimitingFilterTest() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(20);
        CountDownLatch ready = new CountDownLatch(20);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(20);

        ConcurrentHashMap<String, Object> sessionAttributes = new ConcurrentHashMap<>();

        when(session.getAttribute(anyString())).thenAnswer(invocation -> {
            String key = invocation.getArgument(0);
            return sessionAttributes.get(key);
        });

        doAnswer(invocation -> {
            String key = invocation.getArgument(0);
            Object value = invocation.getArgument(1);
            sessionAttributes.put(key, value);
            return null;
        }).when(session).setAttribute(anyString(), any());

        for (int i = 0; i < 20; i++) {
            executor.submit(() -> {
                try {
                    ready.countDown();
                    start.await();

                    try {
                        filter.doFilter(request, response, filterChain);
                    } catch (ResponseStatusException e) {
                        // expected once rate limit is hit
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    done.countDown();
                }
            });
        }

        assertTrue(ready.await(2, TimeUnit.SECONDS));
        start.countDown();
        assertTrue(done.await(5, TimeUnit.SECONDS));

        executor.shutdown();

        ResponseStatusException anotherRequest = assertThrows(ResponseStatusException.class,
                        () -> filter.doFilter(request, response, filterChain));

        assertEquals(429, anotherRequest.getStatusCode().value());
        assertEquals("Too Many Requests", anotherRequest.getReason());
    }
}