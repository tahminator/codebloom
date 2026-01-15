package org.patinanetwork.codebloom.common.jwt;

public class JWTTestObject {

    private String userId;
    private String email;
    private String role;

    public JWTTestObject() {
        this.userId = "123456";
        this.email = "test@example.com";
        this.role = "USER";
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public void setUserId(final String userId) {
        this.userId = userId;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public void setRole(final String role) {
        this.role = role;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final JWTTestObject other = (JWTTestObject) obj;
        if (userId == null) {
            if (other.userId != null) {
                return false;
            }
        } else {
            if (!userId.equals(other.userId)) {
                return false;
            }
        }
        if (email == null) {
            if (other.email != null) {
                return false;
            }
        } else {
            if (!email.equals(other.email)) {
                return false;
            }
        }
        if (role == null) {
            if (other.role != null) {
                return false;
            }
        } else {
            if (!role.equals(other.role)) {
                return false;
            }
        }
        return true;
    }
}
