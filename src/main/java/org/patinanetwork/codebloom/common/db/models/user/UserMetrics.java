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
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@Jacksonized
@SuperBuilder
@ToString
@EqualsAndHashCode
public class UserMetrics {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String userId;

    @NotNullColumn
    private int points;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NullColumn
    @Builder.Default
    private Optional<OffsetDateTime> deletedAt = Optional.empty();
}
