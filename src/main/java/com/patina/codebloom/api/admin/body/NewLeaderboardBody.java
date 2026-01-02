package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class NewLeaderboardBody {

    @NotBlank
    private String name;

    private OffsetDateTime shouldExpireBy;

    private String syntaxHighlightingLanguage;
}
