package com.patina.codebloom.common.db.models;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@ToString
public class Session {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String userId;

    @NotNullColumn
    private LocalDateTime expiresAt;

    // public Session(final String userId, final LocalDateTime expiresAt) {
    // this.userId = userId;
    // this.expiresAt = expiresAt;
    // }
}
