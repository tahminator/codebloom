package org.patinanetwork.codebloom.common.reporter.report.location;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Location {
    BACKEND("Backend"),
    FRONTEND("Frontend"),
    UNKNOWN("Unknown");

    private final String resolvedName;
}
