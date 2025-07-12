package com.patina.codebloom.common.schools;

import com.patina.codebloom.common.db.models.usertag.Tag;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SchoolEnum {
    HUNTER("@myhunter.cuny.edu", Tag.Hunter),
    NYU("@nyu.edu", Tag.Nyu);

    private final String emailDomain;
    private final Tag internalTag;
}
