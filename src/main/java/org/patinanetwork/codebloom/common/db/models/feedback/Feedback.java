package org.patinanetwork.codebloom.common.db.models.feedback;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
public class Feedback {

    private String id;

    private String title;

    private String description;

    @Builder.Default
    private Optional<String> email = Optional.empty();

    private OffsetDateTime createdAt;
}
