package com.patina.codebloom.common.db.models.discord;

import java.time.OffsetDateTime;

import com.patina.codebloom.common.db.helper.annotations.JoinColumn;
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

    @NullColumn
    private String description;

    @NotNullColumn
    private Tag tag;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NullColumn
    private OffsetDateTime deletedAt;

    @JoinColumn
    private DiscordClubMetadata discordClubMetadata;

}
