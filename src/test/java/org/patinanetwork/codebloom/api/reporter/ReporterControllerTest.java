package org.patinanetwork.codebloom.api.reporter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.patinanetwork.codebloom.api.reporter.body.FeedbackBody;
import org.patinanetwork.codebloom.common.db.models.feedback.Feedback;
import org.patinanetwork.codebloom.common.db.repos.feedback.FeedbackRepository;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.url.ServerUrlUtils;

public class ReporterControllerTest {

    private final ReporterController reporterController;
    private final Reporter reporter = mock(Reporter.class);
    private final Env env = mock(Env.class);
    private final ServerUrlUtils serverUrlUtils = mock(ServerUrlUtils.class);
    private final FeedbackRepository feedbackRepository = mock(FeedbackRepository.class);

    public ReporterControllerTest() {
        this.reporterController = new ReporterController(reporter, env, serverUrlUtils, feedbackRepository);
    }

    private HttpServletRequest createMockRequest(final String origin) {
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Origin")).thenReturn(origin);
        return request;
    }

    @Test
    @DisplayName("Submit feedback with all fields - valid origin")
    void testSubmitFeedbackWithAllFields() {
        String validOrigin = "http://localhost:3000";
        when(env.isProd()).thenReturn(true);
        when(serverUrlUtils.getUrl()).thenReturn(validOrigin);
        doNothing().when(feedbackRepository).createFeedback(any(Feedback.class));

        FeedbackBody feedbackBody = FeedbackBody.builder()
                .title("Test Feedback Title")
                .description("This is test feedback")
                .email("test@example.com")
                .build();

        HttpServletRequest request = createMockRequest(validOrigin);

        var response = reporterController.submitReport(feedbackBody, request);

        var body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("ok", body.getMessage());
        verify(feedbackRepository).createFeedback(any(Feedback.class));
    }

    @Test
    @DisplayName("Submit feedback without email - valid origin")
    void testSubmitFeedbackWithoutEmail() {
        String validOrigin = "http://localhost:3000";
        when(env.isProd()).thenReturn(true);
        when(serverUrlUtils.getUrl()).thenReturn(validOrigin);
        doNothing().when(feedbackRepository).createFeedback(any(Feedback.class));

        FeedbackBody feedbackBody = FeedbackBody.builder()
                .title("Feedback Without Email")
                .description("Test feedback without email")
                .email(null)
                .build();

        HttpServletRequest request = createMockRequest(validOrigin);

        var response = reporterController.submitReport(feedbackBody, request);

        var body = response.getBody();
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(body);
        assertTrue(body.isSuccess());
        assertEquals("ok", body.getMessage());
        verify(feedbackRepository).createFeedback(any(Feedback.class));
    }
}
