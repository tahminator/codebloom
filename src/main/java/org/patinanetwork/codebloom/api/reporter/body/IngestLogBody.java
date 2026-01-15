package org.patinanetwork.codebloom.api.reporter.body;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class IngestLogBody {

    private String info;
}
