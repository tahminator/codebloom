package com.patina.codebloom.common.clubs;

import com.patina.codebloom.common.db.models.usertag.Tag;

public enum ClubEnum {
    GWC("GWC", Tag.Gwc);

    private final String clubSlug;
    private final Tag internalTag;

    ClubEnum(final String clubSlug, final Tag internalTag) {
        this.clubSlug = clubSlug;
        this.internalTag = internalTag;
    }

    public String getClubSlug() {
        return clubSlug;
    }

    public Tag getInternalTag() {
        return internalTag;
    }
}
