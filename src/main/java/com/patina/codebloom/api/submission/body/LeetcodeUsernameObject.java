package com.patina.codebloom.api.submission.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class LeetcodeUsernameObject {
    @NotNull
    @NotBlank
    private String leetcodeUsername;
}
