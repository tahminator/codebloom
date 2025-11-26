package com.patina.codebloom.common.db.models.leaderboard;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;
import java.time.LocalDateTime;
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
}
