package com.patina.codebloom.common.db.models.question;

import com.patina.codebloom.common.db.helper.annotations.NullColumn;
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

    @NullColumn
    private String discordName;

    @NullColumn
    private String leetcodeUsername;

    @NullColumn
    private String nickname;
}
