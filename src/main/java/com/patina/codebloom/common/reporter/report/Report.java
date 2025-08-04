package com.patina.codebloom.common.reporter.report;

import java.nio.charset.StandardCharsets;
import java.util.List;

import com.patina.codebloom.common.reporter.report.location.Location;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class Report {
    @Builder.Default
    private List<String> environments = List.of("N/A");
    @Builder.Default
    private Location location = Location.UNKNOWN;
    @Builder.Default
    private byte[] stackTrace = "N/A".getBytes(StandardCharsets.UTF_8);
}
