package com.patina.codebloom.api.reporter;

import com.patina.codebloom.api.reporter.body.IngestErrorsBody;
import com.patina.codebloom.api.reporter.body.IngestLogBody;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.env.Env;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.common.url.ServerUrlUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Reporter route", description = """
    This controller allows the frontend to report anything at runtime (log or error), which will then be ingested by the server.""")
@RequestMapping("/api/reporter")
public class ReporterController {

    private final Reporter reporter;
    private final Env env;
    private final ServerUrlUtils serverUrlUtils;

    public ReporterController(final Reporter reporter, final Env env, final ServerUrlUtils serverUrlUtils) {
        this.reporter = reporter;
        this.env = env;
        this.serverUrlUtils = serverUrlUtils;
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

        reporter.error("reporter error", Report.builder()
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

        reporter.log("reporter log", Report.builder()
                .data(ingestLogBody.getInfo())
                .environments(env.getActiveProfiles())
                .location(Location.FRONTEND)
                .build());
        return ResponseEntity.ok(ApiResponder.success("ok", Empty.of()));
    }
}
