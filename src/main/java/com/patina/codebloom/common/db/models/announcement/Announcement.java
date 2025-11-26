package com.patina.codebloom.common.db.models.announcement;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import com.patina.codebloom.common.db.helper.annotations.NullColumn;
import java.time.OffsetDateTime;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Announcement {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private OffsetDateTime createdAt;

    @NotNullColumn
    private OffsetDateTime expiresAt;

    @NullColumn
    private boolean showTimer;

    @NotNullColumn
    private String message;
}
