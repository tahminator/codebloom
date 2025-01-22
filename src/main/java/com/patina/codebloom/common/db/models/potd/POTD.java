package com.patina.codebloom.common.db.models.potd;

import java.time.LocalDateTime;

public class POTD {
    private String id;
    private String title;
    private String slug;
    private float multiplier;
    private LocalDateTime createdAt;

    public POTD(String id, String title, String slug, float multiplier, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.multiplier = multiplier;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(float multiplier) {
        this.multiplier = multiplier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
