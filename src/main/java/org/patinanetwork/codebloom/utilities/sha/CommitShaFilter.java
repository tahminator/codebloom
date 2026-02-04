package org.patinanetwork.codebloom.utilities.sha;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.patinanetwork.codebloom.common.env.Env;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Filter that adds the X-Commit-Sha header to all HTTP responses. The commit SHA is injected from the application
 * properties, which is populated from the Maven build-time property during Docker build.
 */
@Component
@EnableConfigurationProperties(CommitShaProperties.class)
public class CommitShaFilter implements Filter {

    private final CommitShaProperties commitShaProperties;
    private final Env env;

    public CommitShaFilter(CommitShaProperties commitShaProperties, Env env) {
        this.commitShaProperties = commitShaProperties;
        this.env = env;
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        if (response instanceof HttpServletResponse httpResponse) {
            httpResponse.setHeader("X-Commit-Sha", commitShaProperties.getSha());
        }
        chain.doFilter(request, response);
    }
}
