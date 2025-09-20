package com.patina.codebloom.api;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patina.codebloom.common.dto.ApiResponder;
import com.patina.codebloom.common.reporter.Reporter;
import com.patina.codebloom.common.reporter.report.Report;
import com.patina.codebloom.common.reporter.report.location.Location;
import com.patina.codebloom.utilities.ServerMetadataObject;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@Tag(name = "Basic server metadata")
public class ApiController {

    private final Reporter reporter;

    public ApiController(final Reporter reporter) {
        this.reporter = reporter;
    }

    @Operation(summary = "Basic metadata about the server")
    @GetMapping()
    public ResponseEntity<ApiResponder<ServerMetadataObject>> apiIndex(final HttpServletRequest request) {
        reporter.log(
                        Report.builder()
                                        .data("This is a test")
                                        .environments(List.of("Testenv"))
                                        .location(Location.BACKEND)
                                        .build());

        return ResponseEntity.ok().body(ApiResponder.success("Hello from Codebloom!", new ServerMetadataObject()));
    }

}
