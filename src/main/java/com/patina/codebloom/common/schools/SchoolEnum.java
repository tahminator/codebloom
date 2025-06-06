package com.patina.codebloom.common.schools;

public enum SchoolEnum {
    HUNTER("@myhunter.cuny.edu"),
    NYU("@nyu.edu");

    private final String emailDomain;

    SchoolEnum(String emailDomain) {
        this.emailDomain = emailDomain;
    }

    public String getEmailDomain() {
        return emailDomain;
    }
}