package com.patina.codebloom.website.leetcode.client.model;

public class LeetcodeLang {
    private String name;
    private String verboseName;

    public LeetcodeLang(final String name, final String verboseName) {
        this.name = name;
        this.verboseName = verboseName;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getVerboseName() {
        return verboseName;
    }

    public void setVerboseName(final String verboseName) {
        this.verboseName = verboseName;
    }
}
