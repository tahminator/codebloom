package org.patinanetwork.codebloom.common.db.repos.user.options;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserFilterOptions {

    /** null indicates no preference to time. */
    @Builder.Default
    private OffsetDateTime pointOfTime = null;

    public static final UserFilterOptions DEFAULT = builder().build();
}
