package com.patina.codebloom.common.db.models.question;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import com.patina.codebloom.common.db.helper.annotations.NullColumn;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuestionWithUser extends Question {
    @NullColumn
    private String discordName;

    @NullColumn
    private String leetcodeUsername;

    @NullColumn
    private String nickname;
}
