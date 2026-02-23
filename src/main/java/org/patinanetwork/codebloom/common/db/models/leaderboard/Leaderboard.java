package org.patinanetwork.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Leaderboard {

    private String id;

    private String name;

    private LocalDateTime createdAt;

    @Builder.Default
    private Optional<LocalDateTime> deletedAt = Optional.empty();

    @Builder.Default
    private Optional<LocalDateTime> shouldExpireBy = Optional.empty();

    @Builder.Default
    private Optional<String> syntaxHighlightingLanguage = Optional.empty();
}
