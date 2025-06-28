package com.patina.codebloom.common.schools;
import com.patina.codebloom.common.db.models.usertag.Tag;

public enum SchoolEnum {
    HUNTER("@myhunter.cuny.edu", Tag.Hunter),
    NYU("@nyu.edu", Tag.Nyu);

    private final String emailDomain;
    private final Tag internalTag;

    SchoolEnum(final String emailDomain, final Tag internalTag) {
        this.emailDomain = emailDomain;
        this.internalTag = internalTag;
    }

    public String getEmailDomain() {
        return emailDomain;
    }
    public Tag getInternalTag() {
        return internalTag;
    }
}