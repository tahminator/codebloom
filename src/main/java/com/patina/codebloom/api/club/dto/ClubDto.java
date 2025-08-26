package com.patina.codebloom.api.club.dto;

import com.patina.codebloom.common.db.models.usertag.Tag;

import lombok.Value;
import lombok.Builder;

@Value
@Builder
public class ClubDto {
    String id;
    String name;
    String description;
    String slug;
    String splashIconUrl;
    Tag tag;
}