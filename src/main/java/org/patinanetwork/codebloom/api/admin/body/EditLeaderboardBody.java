package org.patinanetwork.codebloom.api.admin.body;

import com.google.common.base.Strings;
import java.time.OffsetDateTime;
import lombok.*;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.patinanetwork.codebloom.utilities.exception.ValidationException;

@Getter
@Builder
@Jacksonized
@ToString
public class EditLeaderboardBody {

    private String name;

    private OffsetDateTime shouldExpireBy;

    private String syntaxHighlightingLanguage;

    public void validate() {
        var leaderboardName = getName();
        var expire = getShouldExpireBy();

        if (Strings.isNullOrEmpty(leaderboardName)) {
            throw new ValidationException("Leaderboard name cannot be null or empty");
        }

        if (leaderboardName.length() == 1) {
            throw new ValidationException("Leaderboard name cannot have only 1 character");
        }

        if (leaderboardName.length() > 512) {
            throw new ValidationException("Leaderboard name cannot have more than 512 characters");
        }

        if (expire != null) {
            OffsetDateTime nowWithOffset = StandardizedOffsetDateTime.now();
            OffsetDateTime expiresAtWithOffset = StandardizedOffsetDateTime.normalize(expire);
            boolean isInFuture = nowWithOffset.isBefore(expiresAtWithOffset);

            if (!isInFuture) {
                throw new ValidationException("The expiration date must be in the future");
            }
        }
    }
}
