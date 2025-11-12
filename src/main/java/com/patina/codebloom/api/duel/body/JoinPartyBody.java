package com.patina.codebloom.api.duel.body;

import jakarta.validation.constraints.Size;
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
public class JoinPartyBody {
    @Size(min = 6, max = 6, message = "Party code must be exactly 6 digits.")
    private final String partyCode;
}
