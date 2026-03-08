package org.patinanetwork.codebloom.common.db.models.question;

import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuestionWithUser extends Question {

    @Builder.Default
    private Optional<String> discordName = Optional.empty();

    @Builder.Default
    private Optional<String> leetcodeUsername = Optional.empty();

    @Builder.Default
    private Optional<String> nickname = Optional.empty();
}
