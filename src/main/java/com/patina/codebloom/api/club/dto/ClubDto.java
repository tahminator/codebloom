package com.patina.codebloom.api.club.dto;

import com.patina.codebloom.common.db.models.usertag.Tag;

// Club data that is safe to expose
public record ClubDto(
        String id,
        String name,
        String description,
        String slug,
        String splashIconUrl,
        Tag tag 
) {}