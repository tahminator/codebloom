package org.patinanetwork.codebloom.common.db.repos.user.options;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Builder
@Jacksonized
@Getter
@ToString
@EqualsAndHashCode
public class UserMetricsFilterOptions {

    @Builder.Default
    private final int page = 1;

    @Builder.Default
    private final int pageSize = 0;

    @Builder.Default
    private final OffsetDateTime from = null;

    @Builder.Default
    private final OffsetDateTime to = null;

    public static final UserMetricsFilterOptions DEFAULT =
            UserMetricsFilterOptions.builder().build();
}
