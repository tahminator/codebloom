package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RegisterClubBody {

    @NotBlank
    private String userId;

    @NotNull
    private String password;

    @NotNull
    private String clubSlug;
}
