package org.patinanetwork.codebloom.common.db.models;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;

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
