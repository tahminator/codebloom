package org.patinanetwork.codebloom.utilities;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Filter that adds the X-Commit-Sha header to all HTTP responses. The commit SHA is injected from the application
 * properties, which is populated from the Maven build-time property during Docker build.
 */
@Component
public class CommitShaFilter implements Filter {

    @Value("${app.commit.sha:unknown}")
    private String commitSha;

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("X-Commit-Sha", commitSha);
        }
        chain.doFilter(request, response);
    }
}
