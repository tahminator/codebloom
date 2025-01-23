package com.patina.codebloom.utilities;

import java.io.IOException;

import org.springframework.stereotype.Component;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class FakeLag implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization logic if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest) {
            try {
                Thread.sleep(1000); // Introduce a 1-second delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore the interrupt status
                throw new ServletException("Thread interrupted while stalling request", e);
            }
        }
        chain.doFilter(request, response); // Continue with the next filter in the chain
    }

    @Override
    public void destroy() {
        // Cleanup logic if needed
    }
}