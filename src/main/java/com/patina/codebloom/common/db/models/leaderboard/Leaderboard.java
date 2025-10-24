package com.patina.codebloom.common.db.models.leaderboard;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
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

    @NotNullColumn
    private LocalDateTime shouldExpireBy;
}
