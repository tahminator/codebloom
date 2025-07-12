package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class UpdateAdminBody {
    @NotBlank
    private String id;

    @NotNull
    private Boolean toggleTo;
}
