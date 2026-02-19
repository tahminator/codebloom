package org.patinanetwork.codebloom.common.components;

import lombok.Getter;

@Getter
public class LeaderboardException extends Exception {
    private final String title;
    private final String description;

    public LeaderboardException(Throwable t) {
        this("Something went wrong!", t.getMessage());
    }

    public LeaderboardException(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
