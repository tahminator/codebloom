package org.patinanetwork.codebloom.utilities;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class StaticContentFilter implements Filter {

    private List<String> fileExtensions = Arrays.asList(
            "html",
            "js",
            "json",
            "csv",
            "css",
            "png",
            "svg",
            "eot",
            "ttf",
            "woff",
            "appcache",
            "jpg",
            "jpeg",
            "gif",
            "ico",
            "txt");

    @Override
    public final void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(final HttpServletRequest request, final HttpServletResponse response, final FilterChain chain)
            throws IOException, ServletException {
        String path = request.getServletPath();

        boolean isApi = path.startsWith("/api");
        boolean isOpenAPI = path.startsWith("/v3");
        boolean isActuator = path.startsWith("/actuator");
        boolean isResourceFile =
                !isApi && !isOpenAPI && !isActuator && fileExtensions.stream().anyMatch(path::contains);

        if (isApi || isResourceFile || isOpenAPI || isActuator) {
            chain.doFilter(request, response);
        } else {
            request.getRequestDispatcher("/").forward(request, response);
        }
    }
}
