package com.patina.codebloom.api.auth.body;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
public class EmailBody {
    @Size(min = 1, max = 230)
    @NotBlank()
    private String email;
    public EmailBody(final String email) {
        this.email = email;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(final String email) {
        this.email = email;
    }
}
