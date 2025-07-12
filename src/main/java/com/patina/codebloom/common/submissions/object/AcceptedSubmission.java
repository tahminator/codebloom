package com.patina.codebloom.common.submissions.object;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AcceptedSubmission {
    private String title;
    private int points;
}
