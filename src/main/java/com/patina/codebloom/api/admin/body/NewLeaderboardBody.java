package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class NewLeaderboardBody {
    @NotBlank
    private String name;
}
