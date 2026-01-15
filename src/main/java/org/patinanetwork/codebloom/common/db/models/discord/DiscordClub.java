package org.patinanetwork.codebloom.common.db.models.discord;

import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.helper.annotations.JoinColumn;
import org.patinanetwork.codebloom.common.db.models.usertag.Tag;

@Setter
@Getter
@Builder
@ToString
@EqualsAndHashCode
@Jacksonized
public class DiscordClub {

    private String id;

    private String name;

    @Builder.Default
    private Optional<String> description = Optional.empty();

    private Tag tag;

    private OffsetDateTime createdAt;

    @Builder.Default
    private Optional<OffsetDateTime> deletedAt = Optional.empty();

    @JoinColumn
    @Builder.Default
    private Optional<DiscordClubMetadata> discordClubMetadata = Optional.empty();
}
