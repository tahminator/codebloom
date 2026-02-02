package org.patinanetwork.codebloom.api.reporter.body;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class FeedbackBody {

    private String title;

    private String description;

    private String email;
}
