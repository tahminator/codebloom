package com.patina.codebloom.common.leetcode.models;

public class Lang {
    private String name;
    private String verboseName;

    public Lang(String name, String verboseName) {
        this.name = name;
        this.verboseName = verboseName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVerboseName() {
        return verboseName;
    }

    public void setVerboseName(String verboseName) {
        this.verboseName = verboseName;
    }
}
