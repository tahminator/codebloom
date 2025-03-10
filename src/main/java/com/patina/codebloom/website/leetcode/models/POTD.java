package com.patina.codebloom.website.leetcode.models;

import java.time.LocalDateTime;

public class POTD {
    private String id;
    private String title;
    private String slug;
    private float multiplier;
    private LocalDateTime createdAt;

    public POTD(final String id, final String title, final String slug, final float multiplier, final LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.slug = slug;
        this.multiplier = multiplier;
        this.createdAt = createdAt;
    }

    public POTD(final String title, final String slug, final float multiplier, final LocalDateTime createdAt) {
        this.title = title;
        this.slug = slug;
        this.multiplier = multiplier;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(final String slug) {
        this.slug = slug;
    }

    public float getMultiplier() {
        return multiplier;
    }

    public void setMultiplier(final float multiplier) {
        this.multiplier = multiplier;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
