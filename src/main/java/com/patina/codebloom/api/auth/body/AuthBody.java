package com.patina.codebloom.api.auth.body;

import jakarta.validation.constraints.Max;

public class AuthBody {

    @Max(255)
    private String nickname;

    public AuthBody(final String nickname) {
        this.nickname = nickname;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(final String nickname) {
        this.nickname = nickname;
    }
}
