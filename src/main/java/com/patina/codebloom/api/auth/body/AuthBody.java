package com.patina.codebloom.api.auth.body;

import jakarta.validation.constraints.Max;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class AuthBody {
    @Max(255)
    private String nickname;
}
