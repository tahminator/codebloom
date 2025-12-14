package com.patina.codebloom.utilities;

import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class StaticContentFilterTest {

    private StaticContentFilter filter;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain chain;

    @Mock
    private RequestDispatcher dispatcher;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        filter = new StaticContentFilter();
    }

    @Test
    void testApiPathIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testApiNestedPathIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/some/nested/path");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testOpenApiPathIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/v3/api-docs");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testActuatorPathIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/actuator/health");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testHtmlFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/index.html");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testJsFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/assets/index.js");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testCssFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/assets/styles.css");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testPngFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/logo.png");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testSvgFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/icon.svg");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testJsonFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/data.json");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testTxtFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/robots.txt");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testIcoFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/favicon.ico");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testCsvFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/data.csv");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testJpegFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/image.jpeg");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testJpgFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/photo.jpg");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testGifFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/animation.gif");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testEotFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/font.eot");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testTtfFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/font.ttf");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testWoffFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/font.woff");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testAppcacheFileIsAllowedThrough() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/manifest.appcache");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testNonMatchingPathRedirectsToRoot() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/some/random/path");
        when(request.getRequestDispatcher("/")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(request).getRequestDispatcher("/");
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testRootPathRedirectsToRoot() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/");
        when(request.getRequestDispatcher("/")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(request).getRequestDispatcher("/");
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testDashboardPathRedirectsToRoot() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/dashboard");
        when(request.getRequestDispatcher("/")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(request).getRequestDispatcher("/");
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testNestedRouteRedirectsToRoot() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/user/profile/settings");
        when(request.getRequestDispatcher("/")).thenReturn(dispatcher);

        filter.doFilter(request, response, chain);

        verify(request).getRequestDispatcher("/");
        verify(dispatcher).forward(request, response);
        verify(chain, never()).doFilter(request, response);
    }

    @Test
    void testActuatorPathWithExtensionIsNotResourceFile() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/actuator/metrics.json");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testApiPathWithExtensionIsNotResourceFile() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/api/data.json");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }

    @Test
    void testOpenApiPathWithExtensionIsNotResourceFile() throws IOException, ServletException {
        when(request.getServletPath()).thenReturn("/v3/api-docs.json");

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        verify(request, never()).getRequestDispatcher(anyString());
    }
}
