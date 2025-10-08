package com.patina.codebloom.common.db.models.auth;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public class Auth {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String token;

    @NullColumn
    private String csrf;

    @NotNullColumn
    private LocalDateTime createdAt;
}
