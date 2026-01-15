package org.patinanetwork.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode
@ToString
public class Leaderboard {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String name;

    @NotNullColumn
    private LocalDateTime createdAt;

    @NullColumn
    private LocalDateTime deletedAt;

    @NullColumn
    private LocalDateTime shouldExpireBy;

    @NullColumn
    private String syntaxHighlightingLanguage;
}
