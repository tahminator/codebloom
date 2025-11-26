package com.patina.codebloom.common.db.repos.usertag.options;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
@EqualsAndHashCode
public class UserTagFilterOptions {

    /**
     * null indicates no preference to time.
     */
    @Builder.Default
    private OffsetDateTime pointOfTime = null;
}
