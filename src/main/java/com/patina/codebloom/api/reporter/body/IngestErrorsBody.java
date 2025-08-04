package com.patina.codebloom.api.reporter.body;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class IngestErrorsBody {
    private List<String> traces;
}
