package com.patina.codebloom.common.schools;

import com.patina.codebloom.common.db.models.usertag.Tag;

public enum SchoolEnum {
    HUNTER("@myhunter.cuny.edu", Tag.Hunter),
    NYU("@nyu.edu", Tag.Nyu),
    BARUCH("@baruchmail.cuny.edu", Tag.Baruch),
    RPI("@rpi.edu", Tag.Rpi),
    SBU("@stonybrook.edu", Tag.Sbu),
    CCNY("@citymail.cuny.edu", Tag.Ccny),
    COLUMBIA("@columbia.edu", Tag.Columbia);

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