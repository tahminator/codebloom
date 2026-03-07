package org.patinanetwork.codebloom.common.db.models.user;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@ToString
@EqualsAndHashCode
public class UserMetrics {

    private String id;

    private String userId;

    private int points;

    private OffsetDateTime createdAt;

    @Builder.Default
    private Optional<OffsetDateTime> deletedAt = Optional.empty();
}
