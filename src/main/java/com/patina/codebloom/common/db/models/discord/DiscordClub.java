package com.patina.codebloom.common.db.models.discord;

import java.time.LocalDateTime;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;
import com.patina.codebloom.common.db.models.usertag.Tag;

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
public class DiscordClub {
    @NotNullColumn
    private String id;

    @NotNullColumn
    private String name;

    private String description;

    @NotNullColumn
    private Tag tag;

    @NotNullColumn
    private LocalDateTime createdAt;

    @NullColumn
    private LocalDateTime deletedAt;

}
