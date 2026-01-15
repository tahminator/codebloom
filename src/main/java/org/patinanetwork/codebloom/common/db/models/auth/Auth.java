package org.patinanetwork.codebloom.common.db.models.auth;

import java.time.OffsetDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.db.helper.annotations.NotNullColumn;
import org.patinanetwork.codebloom.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@Builder
@Jacksonized
@EqualsAndHashCode
@ToString
public class Auth {

    @NotNullColumn
    private String id;

    @NotNullColumn
    private String token;

    @NullColumn
    private String csrf;

    @NotNullColumn
    private OffsetDateTime createdAt;
}
