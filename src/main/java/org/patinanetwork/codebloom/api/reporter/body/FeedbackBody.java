package org.patinanetwork.codebloom.api.reporter.body;

import com.google.common.base.Strings;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;
import org.patinanetwork.codebloom.utilities.exception.ValidationException;

@Getter
@Builder
@Jacksonized
public class FeedbackBody {

    private String title;

    private String description;

    private String email;

    public void validate() {
        if (Strings.isNullOrEmpty(description)) {
            throw new ValidationException("Description may not be null or empty.");
        }

        if (description.length() < 10) {
            throw new ValidationException("Description must be at least 10 characters.");
        }

        if (description.length() > 10000) {
            throw new ValidationException("Description must not exceed 10000 characters.");
        }
    }
}
