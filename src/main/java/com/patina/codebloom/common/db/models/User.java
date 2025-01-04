package com.patina.codebloom.common.db.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    private String id;

    // Even though discord IDs are integers, they are very large so we just use
    // String instead.
    private String discordId;
    private String discordName;

    public User(String id, String discordId, String discordName) {
        this.id = id;
        this.discordId = discordId;
        this.discordName = discordName;
    }

    public User(String discordId, String discordName) {
        this.discordId = discordId;
        this.discordName = discordName;
    }
}
