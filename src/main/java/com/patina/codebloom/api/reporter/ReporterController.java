package com.patina.codebloom.api.reporter;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.patina.codebloom.api.reporter.body.IngestErrorsBody;
import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.dto.Empty;
import com.patina.codebloom.common.reporter.ErrorReporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Error reporting route", description = "This controller allows the frontend to report any runtime errors, which will then be ingested by the server.")
@RequestMapping("/api/reporting")
public class ReporterController {
    private final ErrorReporter errorReporter;

    public ReporterController(final ErrorReporter errorReporter) {
        this.errorReporter = errorReporter;
    }

    @PostMapping("")
    public ResponseEntity<ApiResponder<Empty>> ingestErrors(final @RequestBody IngestErrorsBody ingestErrorsBody) {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        // List<String> traces = ingestErrorsBody.getTraces();
        //
        // if (traces.size() > 3) {
        // String combined = String.join("\n\n", traces);
        // errorReporter.report(Report.builder()
        // .location(Location.FRONTEND)
        // .stackTrace(combined.getBytes())
        // .build());
        // } else {
        // for (String trace : traces) {
        // errorReporter.report(Report.builder()
        // .location(Location.FRONTEND)
        // .stackTrace(trace.getBytes())
        // .build());
        // }
        // }
        //
        // return ResponseEntity.ok(ApiResponder.success("Received!", Empty.of()));
    }
}
