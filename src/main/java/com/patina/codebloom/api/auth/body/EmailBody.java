package com.patina.codebloom.api.auth.body;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import jakarta.validation.constraints.NotBlank;

@Getter
@Builder
@Jacksonized
public class EmailBody {
    @Size(min = 1, max = 230)
    @NotBlank()
    private String email;
}
