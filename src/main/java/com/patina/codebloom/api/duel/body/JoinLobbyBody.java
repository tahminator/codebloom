package com.patina.codebloom.api.duel.body;

import com.google.common.base.Strings;
import com.patina.codebloom.utilities.exception.ValidationException;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
@ToString
@EqualsAndHashCode
public class JoinLobbyBody {
    private final String partyCode;

    public void validate() {
        var code = getPartyCode();
        if (Strings.isNullOrEmpty(code)) {
            throw new ValidationException("Lobby code may not be null or empty.");
        }

        if (code.length() != 6) {
            throw new ValidationException("Lobby code must be exactly 6 characters.");
        }
    }
}
