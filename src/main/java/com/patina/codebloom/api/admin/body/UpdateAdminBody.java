package com.patina.codebloom.api.admin.body;

import jakarta.validation.constraints.NotBlank;

public class UpdateAdminBody {
    @NotBlank
    private String id;

    @NotBlank
    private Boolean toggleTo;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public Boolean getToggleTo() {
        return toggleTo;
    }

    public void setToggleTo(final Boolean toggleTo) {
        this.toggleTo = toggleTo;
    }
}
