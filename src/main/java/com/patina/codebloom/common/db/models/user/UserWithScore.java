package com.patina.codebloom.common.db.models.user;

import java.util.ArrayList;

import com.patina.codebloom.common.db.models.usertag.UserTag;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    private int totalScore;
}
