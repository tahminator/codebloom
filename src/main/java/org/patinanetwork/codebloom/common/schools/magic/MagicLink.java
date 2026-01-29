package org.patinanetwork.codebloom.common.schools.magic;

public class MagicLink {

    private String email;
    private String userId;
    private String issuer;

    public MagicLink() {}

    public MagicLink(final String email, final String userId) {
        this.email = email;
        this.userId = userId;
    }

    public MagicLink(final String email, final String userId, final String issuer) {
        this.email = email;
        this.userId = userId;
        this.issuer = issuer;
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

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(final String issuer) {
        this.issuer = issuer;
    }
}
