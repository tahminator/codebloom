package org.patinanetwork.codebloom.api.reporter;

import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.patinanetwork.codebloom.api.reporter.body.FeedbackBody;
import org.patinanetwork.codebloom.api.reporter.body.IngestErrorsBody;
import org.patinanetwork.codebloom.api.reporter.body.IngestLogBody;
import org.patinanetwork.codebloom.common.db.models.feedback.Feedback;
import org.patinanetwork.codebloom.common.db.repos.feedback.FeedbackRepository;
import org.patinanetwork.codebloom.common.dto.ApiResponder;
import org.patinanetwork.codebloom.common.dto.Empty;
import org.patinanetwork.codebloom.common.env.Env;
import org.patinanetwork.codebloom.common.reporter.Reporter;
import org.patinanetwork.codebloom.common.reporter.report.Report;
import org.patinanetwork.codebloom.common.reporter.report.location.Location;
import org.patinanetwork.codebloom.common.url.ServerUrlUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reporter route", description = """
    This controller allows the frontend to report anything at runtime (log or error), which will then be ingested by the server.""")
@RequestMapping("/api/reporter")
@Timed(value = "controller.execution")
public class ReporterController {

    private final Reporter reporter;
    private final Env env;
    private final ServerUrlUtils serverUrlUtils;
    private final FeedbackRepository feedbackRepository;

    public ReporterController(
            final Reporter reporter,
            final Env env,
            final ServerUrlUtils serverUrlUtils,
            final FeedbackRepository feedbackRepository) {
        this.reporter = reporter;
        this.env = env;
        this.serverUrlUtils = serverUrlUtils;
        this.feedbackRepository = feedbackRepository;
    }

    private boolean validateOrigin(final HttpServletRequest request) {
        if (!env.isProd()) {
            return true;
        }

        String origin = request.getHeader("Origin");
        String serverUrl = serverUrlUtils.getUrl();

        if (origin == null) {
            return false;
        }

        if (!origin.equals(serverUrl)) {
            return false;
        }

        return true;
    }

    @PostMapping("/error")
    public ResponseEntity<ApiResponder<Empty>> ingestErrors(
            final @RequestBody IngestErrorsBody ingestErrorBody, final HttpServletRequest request) {
        if (!validateOrigin(request)) {
            // don't let the bad actor know it failed.
            return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
        }

        reporter.error(
                "reporter error",
                Report.builder()
                        .data(ingestErrorBody.getTrace())
                        .environments(env.getActiveProfiles())
                        .location(Location.FRONTEND)
                        .build());
        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @PostMapping("/log")
    public ResponseEntity<ApiResponder<Empty>> ingestLog(
            final @RequestBody IngestLogBody ingestLogBody, final HttpServletRequest request) {
        if (!validateOrigin(request)) {
            // don't let the bad actor know it failed.
            return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
        }

        reporter.log(
                "reporter log",
                Report.builder()
                        .data(ingestLogBody.getInfo())
                        .environments(env.getActiveProfiles())
                        .location(Location.FRONTEND)
                        .build());
        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }

    @PostMapping("/feedback")
    public ResponseEntity<ApiResponder<Empty>> submitFeedback(
            final @RequestBody FeedbackBody feedbackBody, final HttpServletRequest request) {
        feedbackBody.validate();

        if (!validateOrigin(request)) {
            // don't let the bad actor know it failed.
            return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
        }

        feedbackBody.validate();

        Feedback feedback = Feedback.builder()
                .title(feedbackBody.getTitle())
                .description(feedbackBody.getDescription())
                .email(Optional.ofNullable(feedbackBody.getEmail()))
                .build();

        feedbackRepository.createFeedback(feedback);
        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }
}
