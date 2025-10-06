package com.patina.codebloom.common.db.models.user;

import com.patina.codebloom.common.db.helper.annotations.NotNullColumn;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserWithScore extends User {
    @NotNullColumn
    private int totalScore;
}
