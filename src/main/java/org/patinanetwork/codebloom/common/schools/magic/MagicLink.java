package org.patinanetwork.codebloom.common.schools.magic;

public class MagicLink {

    private String email;
    private String userId;

    public MagicLink() {}

    public MagicLink(final String email, final String userId) {
        this.email = email;
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }
}
