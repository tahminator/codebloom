package org.patinanetwork.codebloom.common.reporter.report;

import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.reporter.report.location.Location;

@Getter
@Builder
@Jacksonized
public class Report {

    /** @see {@link org.patinanetwork.codebloom.common.env.Env} */
    @Builder.Default
    private List<String> environments = List.of("N/A");

    @Builder.Default
    private Location location = Location.UNKNOWN;

    @Builder.Default
    private String data = "N/A";
}
