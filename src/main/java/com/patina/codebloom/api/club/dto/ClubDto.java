package com.patina.codebloom.api.club.dto;

import com.patina.codebloom.common.db.models.usertag.Tag;
import lombok.Builder;
import lombok.Getter;
import lombok.Value;

@Value
@Getter
@Builder
public class ClubDto {

    private String id;
    private String name;
    private String description;
    private String slug;
    private String splashIconUrl;
    private Tag tag;
}
