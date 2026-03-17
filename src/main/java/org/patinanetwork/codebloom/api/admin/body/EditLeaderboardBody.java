package org.patinanetwork.codebloom.api.admin.body;

import com.google.common.base.Strings;
import jakarta.validation.constraints.NotBlank;
import java.time.OffsetDateTime;
import lombok.*;
import org.patinanetwork.codebloom.common.time.StandardizedOffsetDateTime;
import org.patinanetwork.codebloom.utilities.exception.ValidationException;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class EditLeaderboardBody {

    @NotBlank
    private String name;

    private OffsetDateTime shouldExpireBy;

    private String syntaxHighlightingLanguage;

    public void validate() {
        var name = getName();
        var expire = getShouldExpireBy();
        var highlight = getSyntaxHighlightingLanguage();

        if (Strings.isNullOrEmpty(name)) {
            throw new ValidationException("Leaderboard name cannot be null or empty");
        }

        if (name.length() == 1) {
            throw new ValidationException("Leaderboard name cannot have only 1 character");
        }

        if (name.length() > 512) {
            throw new ValidationException("Leaderboard name cannot have more than 512 characters");
        }

        OffsetDateTime nowWithOffset = StandardizedOffsetDateTime.now();
        OffsetDateTime expiresAtWithOffset = StandardizedOffsetDateTime.normalize(expire);
        boolean isInFuture = nowWithOffset.isBefore(expiresAtWithOffset);
        if (!isInFuture) {
            throw new ValidationException("The expiration date must be in the future");
        }

        if (Strings.isNullOrEmpty(highlight)) {
            throw new ValidationException("Syntax highlight language cannot be null or empty");
        }
    }
}
